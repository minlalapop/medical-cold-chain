package com.medical.alert.dto;

import java.time.LocalDate;
import lombok.Data;

@Data
public class BatchResponse {

    private Long id;
    private String batchNumber;
    private int quantity;
    private LocalDate expiryDate;

    private ProductDto product;
    private LocationDto location;

    @Data
    public static class ProductDto {
        private Long id;
        private String name;
        private boolean requiresColdChain;
        private int minStockThreshold;
    }

    @Data
    public static class LocationDto {
        private Long id;
        private String name;
    }
}
