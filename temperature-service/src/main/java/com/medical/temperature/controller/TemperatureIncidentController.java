package com.medical.temperature.controller;

import com.medical.temperature.dto.ResolveIncidentRequest;
import com.medical.temperature.entity.TemperatureIncident;
import com.medical.temperature.service.TemperatureIncidentService;
import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/temperatures/incidents")
@RequiredArgsConstructor
public class TemperatureIncidentController {

    private final TemperatureIncidentService incidentService;

    @GetMapping
    public List<TemperatureIncident> getAllIncidents() {
        return incidentService.getAllIncidents();
    }

    @GetMapping("/open")
    public List<TemperatureIncident> getOpenIncidents() {
        return incidentService.getOpenIncidents();
    }

    @GetMapping("/{id}")
    public TemperatureIncident getIncidentById(@PathVariable Long id) {
        return incidentService.getIncidentById(id);
    }

    @GetMapping("/location/{locationId}")
    public List<TemperatureIncident> getIncidentsByLocation(@PathVariable Long locationId) {
        return incidentService.getIncidentsByLocation(locationId);
    }

    @PutMapping("/{id}/resolve")
    public TemperatureIncident resolveIncident(@PathVariable Long id,
                                               @Valid @RequestBody ResolveIncidentRequest request) {
        return incidentService.resolveIncident(id, request);
    }
}
