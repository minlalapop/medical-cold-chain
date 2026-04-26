package com.medical.inventory.dto;

import com.medical.inventory.enums.LocationType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class LocationRequest {

    @NotBlank
    private String name;

    @NotNull
    private LocationType type;
}
