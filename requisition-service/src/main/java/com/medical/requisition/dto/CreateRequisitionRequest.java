package com.medical.requisition.dto;

import com.medical.requisition.enums.Department;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import java.util.List;
import lombok.Data;

@Data
public class CreateRequisitionRequest {

    @NotBlank
    private String requesterName;

    @NotNull
    private Department department;

    @NotEmpty
    @Valid
    private List<RequisitionItemRequest> items;
}
