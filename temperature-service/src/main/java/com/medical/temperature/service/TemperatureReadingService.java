package com.medical.temperature.service;

import com.medical.temperature.client.AuditClient;
import com.medical.temperature.dto.TemperatureReadingRequest;
import com.medical.temperature.entity.TemperatureIncident;
import com.medical.temperature.entity.TemperatureReading;
import com.medical.temperature.enums.IncidentSeverity;
import com.medical.temperature.enums.IncidentStatus;
import com.medical.temperature.repository.TemperatureReadingRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class TemperatureReadingService {

    private final TemperatureReadingRepository readingRepository;
    private final TemperatureIncidentService incidentService;
    private final AuditClient auditClient;

    @Transactional
    public TemperatureReading createReading(TemperatureReadingRequest request) {
        TemperatureReading reading = TemperatureReading.builder()
                .locationId(request.getLocationId())
                .locationName(request.getLocationName())
                .temperature(request.getTemperature())
                .expectedMin(request.getExpectedMin())
                .expectedMax(request.getExpectedMax())
                .source(request.getSource())
                .build();

        TemperatureReading savedReading = readingRepository.save(reading);
        auditClient.log(
                "temperature-service",
                "TEMPERATURE_READING",
                "TemperatureReading",
                savedReading.getId(),
                "Temperature reading recorded for location " + savedReading.getLocationName()
        );

        if (isOutOfRange(request)) {
            TemperatureIncident incident = TemperatureIncident.builder()
                    .locationId(request.getLocationId())
                    .locationName(request.getLocationName())
                    .temperature(request.getTemperature())
                    .expectedMin(request.getExpectedMin())
                    .expectedMax(request.getExpectedMax())
                    .severity(calculateSeverity(request))
                    .status(IncidentStatus.OPEN)
                    .message(buildIncidentMessage(request))
                    .build();

            TemperatureIncident savedIncident = incidentService.saveIncident(incident);
            auditClient.log(
                    "temperature-service",
                    "TEMPERATURE_INCIDENT",
                    "TemperatureIncident",
                    savedIncident.getId(),
                    savedIncident.getMessage()
            );
        }

        return savedReading;
    }

    public List<TemperatureReading> getAllReadings() {
        return readingRepository.findAll();
    }

    public List<TemperatureReading> getReadingsByLocation(Long locationId) {
        return readingRepository.findByLocationId(locationId);
    }

    private boolean isOutOfRange(TemperatureReadingRequest request) {
        return request.getTemperature() < request.getExpectedMin()
                || request.getTemperature() > request.getExpectedMax();
    }

    private IncidentSeverity calculateSeverity(TemperatureReadingRequest request) {
        double temperature = request.getTemperature();
        double min = request.getExpectedMin();
        double max = request.getExpectedMax();

        if (temperature < min - 2 || temperature > max + 2) {
            return IncidentSeverity.CRITICAL;
        }

        return IncidentSeverity.WARNING;
    }

    private String buildIncidentMessage(TemperatureReadingRequest request) {
        return "Temperature out of range at "
                + request.getLocationName()
                + ". Recorded: "
                + request.getTemperature()
                + "C, expected range: "
                + request.getExpectedMin()
                + "C to "
                + request.getExpectedMax()
                + "C.";
    }
}
