package com.medical.requisition.dto;

import lombok.Data;

@Data
public class StockResponse {

    private Long productId;
    private String productName;
    private int totalQuantity;
    private int minStockThreshold;
    private boolean lowStock;
}
