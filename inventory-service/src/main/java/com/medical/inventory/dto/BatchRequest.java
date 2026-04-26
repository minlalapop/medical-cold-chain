package com.medical.inventory.dto;

import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;
import lombok.Data;

@Data
public class BatchRequest {

    @NotBlank
    private String batchName;

    private String batchNumber;

    @Min(1)
    private int quantity;

    @FutureOrPresent
    private LocalDate expiryDate;

    @NotNull
    private Long productId;

    @NotNull
    private Long locationId;
}
