package com.medical.alert.dto;

import lombok.Data;

@Data
public class TemperatureIncidentResponse {

    private Long id;
    private Long locationId;
    private String locationName;

    private double temperature;
    private double expectedMin;
    private double expectedMax;

    private String severity;
    private String status;
    private String message;
}
