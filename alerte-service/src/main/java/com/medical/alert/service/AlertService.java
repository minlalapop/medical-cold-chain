package com.medical.alert.service;

import com.medical.alert.client.AuditClient;
import com.medical.alert.client.InventoryClient;
import com.medical.alert.client.TemperatureClient;
import com.medical.alert.dto.BatchResponse;
import com.medical.alert.dto.ResolveAlertRequest;
import com.medical.alert.dto.TemperatureIncidentResponse;
import com.medical.alert.entity.Alert;
import com.medical.alert.enums.AlertPriority;
import com.medical.alert.enums.AlertStatus;
import com.medical.alert.enums.AlertType;
import com.medical.alert.exception.ResourceNotFoundException;
import com.medical.alert.repository.AlertRepository;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Comparator;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AlertService {

    private final AlertRepository alertRepository;
    private final InventoryClient inventoryClient;
    private final TemperatureClient temperatureClient;
    private final AuditClient auditClient;

    @Scheduled(initialDelay = 15000, fixedDelay = 60000)
    public void refreshAlertsAutomatically() {
        try {
            checkAllAlerts();
        } catch (Exception ignored) {
        }
    }

    public List<Alert> getAllAlerts() {
        return sortByPriority(alertRepository.findAll());
    }

    public List<Alert> getOpenAlerts() {
        return sortByPriority(alertRepository.findByStatus(AlertStatus.OPEN));
    }

    public List<Alert> getAlertsByType(AlertType type) {
        return sortByPriority(alertRepository.findByType(type));
    }

    public Alert getAlertById(Long id) {
        return alertRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Alert not found with id: " + id));
    }

    @Transactional
    public Alert resolveAlert(Long id, ResolveAlertRequest request) {
        Alert alert = getAlertById(id);

        alert.setStatus(AlertStatus.RESOLVED);
        alert.setResolvedAt(LocalDateTime.now());
        alert.setResolvedBy(request.getResolvedBy());
        alert.setResolutionNote(request.getResolutionNote());

        Alert savedAlert = alertRepository.save(alert);
        auditClient.log(
                request.getResolvedBy(),
                "ALERT_RESOLVED",
                "Alert",
                savedAlert.getId(),
                "Alert resolved: " + savedAlert.getTitle()
        );

        return savedAlert;
    }

    @Transactional
    public List<Alert> checkExpiryAlerts() {
        List<BatchResponse> batches = inventoryClient.getAllBatches();

        if (batches == null) {
            return List.of();
        }

        for (BatchResponse batch : batches) {
            if (batch.getQuantity() <= 0 || batch.getExpiryDate() == null) {
                continue;
            }

            long daysUntilExpiry = ChronoUnit.DAYS.between(LocalDate.now(), batch.getExpiryDate());

            if (daysUntilExpiry <= 30) {
                createExpiryAlertIfNotExists(batch, daysUntilExpiry);
            }
        }

        return getOpenAlerts();
    }

    @Transactional
    public List<Alert> checkTemperatureAlerts() {
        List<TemperatureIncidentResponse> incidents = temperatureClient.getOpenTemperatureIncidents();

        if (incidents == null) {
            return List.of();
        }

        for (TemperatureIncidentResponse incident : incidents) {
            createTemperatureAlertIfNotExists(incident);
        }

        return getOpenAlerts();
    }

    @Transactional
    public List<Alert> checkAllAlerts() {
        checkExpiryAlerts();
        checkTemperatureAlerts();
        checkCascadeAlerts();

        return getOpenAlerts();
    }

    private void createExpiryAlertIfNotExists(BatchResponse batch, long daysUntilExpiry) {
        boolean alreadyExists = alertRepository.existsByTypeAndBatchId(
                AlertType.EXPIRY,
                batch.getId()
        );

        if (alreadyExists) {
            return;
        }

        AlertPriority priority = calculateExpiryPriority(daysUntilExpiry);

        Alert alert = Alert.builder()
                .type(AlertType.EXPIRY)
                .priority(priority)
                .title("Product close to expiry")
                .message(buildExpiryMessage(batch, daysUntilExpiry))
                .productId(batch.getProduct() != null ? batch.getProduct().getId() : null)
                .productName(batch.getProduct() != null ? batch.getProduct().getName() : null)
                .batchId(batch.getId())
                .batchNumber(batch.getBatchNumber())
                .locationId(batch.getLocation() != null ? batch.getLocation().getId() : null)
                .locationName(batch.getLocation() != null ? batch.getLocation().getName() : null)
                .build();

        Alert savedAlert = alertRepository.save(alert);
        auditClient.log(
                "alert-service",
                "ALERT_CREATED",
                "Alert",
                savedAlert.getId(),
                "Expiry alert created for batch " + batch.getBatchNumber()
        );
    }

    private void createTemperatureAlertIfNotExists(TemperatureIncidentResponse incident) {
        boolean alreadyExists = alertRepository.existsByTypeAndSourceIncidentId(
                AlertType.TEMPERATURE,
                incident.getId()
        );

        if (alreadyExists) {
            return;
        }

        AlertPriority priority = "CRITICAL".equalsIgnoreCase(incident.getSeverity())
                ? AlertPriority.CRITICAL
                : AlertPriority.HIGH;

        Alert alert = Alert.builder()
                .type(AlertType.TEMPERATURE)
                .priority(priority)
                .title("Temperature incident detected")
                .message(incident.getMessage())
                .locationId(incident.getLocationId())
                .locationName(incident.getLocationName())
                .sourceIncidentId(incident.getId())
                .build();

        Alert savedAlert = alertRepository.save(alert);
        auditClient.log(
                "alert-service",
                "ALERT_CREATED",
                "Alert",
                savedAlert.getId(),
                "Temperature alert created for incident " + incident.getId()
        );
    }

    private void checkCascadeAlerts() {
        List<Alert> openAlerts = alertRepository.findByStatus(AlertStatus.OPEN);

        List<Alert> expiryAlerts = openAlerts.stream()
                .filter(alert -> alert.getType() == AlertType.EXPIRY)
                .toList();

        List<Alert> temperatureAlerts = openAlerts.stream()
                .filter(alert -> alert.getType() == AlertType.TEMPERATURE)
                .toList();

        for (Alert expiryAlert : expiryAlerts) {
            for (Alert temperatureAlert : temperatureAlerts) {
                if (expiryAlert.getLocationId() != null
                        && expiryAlert.getLocationId().equals(temperatureAlert.getLocationId())) {
                    createCascadeAlertIfNotExists(expiryAlert, temperatureAlert);
                }
            }
        }
    }

    private void createCascadeAlertIfNotExists(Alert expiryAlert, Alert temperatureAlert) {
        boolean alreadyExists = alertRepository.findByType(AlertType.CASCADE)
                .stream()
                .anyMatch(alert ->
                        alert.getBatchId() != null
                                && alert.getBatchId().equals(expiryAlert.getBatchId())
                                && alert.getLocationId() != null
                                && alert.getLocationId().equals(temperatureAlert.getLocationId())
                );

        if (alreadyExists) {
            return;
        }

        Alert alert = Alert.builder()
                .type(AlertType.CASCADE)
                .priority(AlertPriority.CRITICAL)
                .title("Cascade alert: expiry risk + temperature incident")
                .message("Critical cascade detected: batch "
                        + expiryAlert.getBatchNumber()
                        + " is close to expiry and stored in a location with an active temperature incident.")
                .productId(expiryAlert.getProductId())
                .productName(expiryAlert.getProductName())
                .batchId(expiryAlert.getBatchId())
                .batchNumber(expiryAlert.getBatchNumber())
                .locationId(expiryAlert.getLocationId())
                .locationName(expiryAlert.getLocationName())
                .build();

        Alert savedAlert = alertRepository.save(alert);
        auditClient.log(
                "alert-service",
                "ALERT_CREATED",
                "Alert",
                savedAlert.getId(),
                "Cascade alert created for batch " + expiryAlert.getBatchNumber()
        );
    }

    private AlertPriority calculateExpiryPriority(long daysUntilExpiry) {
        if (daysUntilExpiry < 0) {
            return AlertPriority.CRITICAL;
        }

        if (daysUntilExpiry <= 7) {
            return AlertPriority.CRITICAL;
        }

        if (daysUntilExpiry <= 15) {
            return AlertPriority.HIGH;
        }

        return AlertPriority.MEDIUM;
    }

    private String buildExpiryMessage(BatchResponse batch, long daysUntilExpiry) {
        if (daysUntilExpiry < 0) {
            return "Batch "
                    + batch.getBatchNumber()
                    + " is already expired.";
        }

        return "Batch "
                + batch.getBatchNumber()
                + " will expire in "
                + daysUntilExpiry
                + " day(s).";
    }

    private List<Alert> sortByPriority(List<Alert> alerts) {
        return alerts.stream()
                .sorted(Comparator.comparing(Alert::getPriority).reversed())
                .toList();
    }
}
