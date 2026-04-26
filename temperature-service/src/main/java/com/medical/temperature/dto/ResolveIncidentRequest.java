package com.medical.temperature.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class ResolveIncidentRequest {

    @NotBlank
    private String resolvedBy;

    private String resolutionNote;
}
