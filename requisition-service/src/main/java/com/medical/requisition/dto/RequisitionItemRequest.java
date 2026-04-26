package com.medical.requisition.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class RequisitionItemRequest {

    @NotNull
    private Long productId;

    private String productName;

    @Min(1)
    private int quantity;
}
