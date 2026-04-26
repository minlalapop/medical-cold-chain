package com.medical.temperature.service;

import com.medical.temperature.client.AuditClient;
import com.medical.temperature.dto.ResolveIncidentRequest;
import com.medical.temperature.entity.TemperatureIncident;
import com.medical.temperature.enums.IncidentStatus;
import com.medical.temperature.exception.ResourceNotFoundException;
import com.medical.temperature.repository.TemperatureIncidentRepository;
import java.time.LocalDateTime;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class TemperatureIncidentService {

    private final TemperatureIncidentRepository incidentRepository;
    private final AuditClient auditClient;

    public TemperatureIncident saveIncident(TemperatureIncident incident) {
        return incidentRepository.save(incident);
    }

    public List<TemperatureIncident> getAllIncidents() {
        return incidentRepository.findAll();
    }

    public List<TemperatureIncident> getOpenIncidents() {
        return incidentRepository.findByStatus(IncidentStatus.OPEN);
    }

    public List<TemperatureIncident> getIncidentsByLocation(Long locationId) {
        return incidentRepository.findByLocationId(locationId);
    }

    public TemperatureIncident getIncidentById(Long id) {
        return incidentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Incident not found with id: " + id));
    }

    public TemperatureIncident resolveIncident(Long id, ResolveIncidentRequest request) {
        TemperatureIncident incident = getIncidentById(id);

        incident.setStatus(IncidentStatus.RESOLVED);
        incident.setResolvedAt(LocalDateTime.now());
        incident.setResolvedBy(request.getResolvedBy());
        incident.setResolutionNote(request.getResolutionNote());

        TemperatureIncident savedIncident = incidentRepository.save(incident);
        auditClient.log(
                request.getResolvedBy(),
                "UPDATE",
                "TemperatureIncident",
                savedIncident.getId(),
                "Temperature incident resolved"
        );

        return savedIncident;
    }
}
