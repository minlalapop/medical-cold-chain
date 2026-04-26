package com.medical.temperature.dto;

import com.medical.temperature.enums.ReadingSource;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class TemperatureReadingRequest {

    @NotNull
    private Long locationId;

    @NotBlank
    private String locationName;

    private double temperature;

    private double expectedMin = 2.0;

    private double expectedMax = 8.0;

    @NotNull
    private ReadingSource source;
}
