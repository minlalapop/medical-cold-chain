package com.medical.inventory.dto;

import com.medical.inventory.enums.Category;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class ProductRequest {

    @NotBlank
    private String name;

    private String description;

    @NotNull
    private Category category;

    private boolean requiresColdChain;

    @Min(0)
    private int minStockThreshold;
}
