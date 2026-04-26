package com.medical.temperature.controller;

import com.medical.temperature.dto.TemperatureReadingRequest;
import com.medical.temperature.entity.TemperatureReading;
import com.medical.temperature.service.TemperatureReadingService;
import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/temperatures/readings")
@RequiredArgsConstructor
public class TemperatureReadingController {

    private final TemperatureReadingService readingService;

    @PostMapping
    public TemperatureReading createReading(@Valid @RequestBody TemperatureReadingRequest request) {
        return readingService.createReading(request);
    }

    @GetMapping
    public List<TemperatureReading> getAllReadings() {
        return readingService.getAllReadings();
    }

    @GetMapping("/location/{locationId}")
    public List<TemperatureReading> getReadingsByLocation(@PathVariable Long locationId) {
        return readingService.getReadingsByLocation(locationId);
    }
}
