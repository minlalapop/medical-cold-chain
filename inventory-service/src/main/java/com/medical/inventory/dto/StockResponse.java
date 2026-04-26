package com.medical.inventory.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class StockResponse {

    private Long productId;
    private String productName;
    private int totalQuantity;
    private int minStockThreshold;
    private boolean lowStock;
}
