package com.medical.alert.controller;

import com.medical.alert.dto.ResolveAlertRequest;
import com.medical.alert.entity.Alert;
import com.medical.alert.enums.AlertType;
import com.medical.alert.service.AlertService;
import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/alerts")
@RequiredArgsConstructor
public class AlertController {

    private final AlertService alertService;

    @GetMapping
    public List<Alert> getAllAlerts() {
        return alertService.getAllAlerts();
    }

    @GetMapping("/open")
    public List<Alert> getOpenAlerts() {
        return alertService.getOpenAlerts();
    }

    @GetMapping("/{id}")
    public Alert getAlertById(@PathVariable Long id) {
        return alertService.getAlertById(id);
    }

    @GetMapping("/type/{type}")
    public List<Alert> getAlertsByType(@PathVariable AlertType type) {
        return alertService.getAlertsByType(type);
    }

    @PostMapping("/check-expiry")
    public List<Alert> checkExpiryAlerts() {
        return alertService.checkExpiryAlerts();
    }

    @PostMapping("/check-temperature")
    public List<Alert> checkTemperatureAlerts() {
        return alertService.checkTemperatureAlerts();
    }

    @PostMapping("/check-all")
    public List<Alert> checkAllAlerts() {
        return alertService.checkAllAlerts();
    }

    @PutMapping("/{id}/resolve")
    public Alert resolveAlert(@PathVariable Long id, @Valid @RequestBody ResolveAlertRequest request) {
        return alertService.resolveAlert(id, request);
    }
}
