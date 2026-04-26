package com.medical.requisition.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ConsumeStockRequest {

    private Long productId;
    private int quantity;
    private String reason;
}
