package com.medical.requisition.dto;

import lombok.Data;

@Data
public class AlertResponse {

    private Long id;
    private String type;
    private String priority;
    private String status;
    private String title;
    private String message;
    private Long productId;
    private String productName;
    private Long batchId;
    private String batchNumber;
}
