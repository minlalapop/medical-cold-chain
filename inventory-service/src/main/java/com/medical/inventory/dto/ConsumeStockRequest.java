package com.medical.inventory.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class ConsumeStockRequest {

    @NotNull
    private Long productId;

    @Min(1)
    private int quantity;

    private String reason;
}
