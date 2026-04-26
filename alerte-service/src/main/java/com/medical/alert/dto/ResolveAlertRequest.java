package com.medical.alert.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class ResolveAlertRequest {

    @NotBlank
    private String resolvedBy;

    private String resolutionNote;
}
