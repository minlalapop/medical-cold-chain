package com.medical.audit.dto;

import com.medical.audit.enums.AuditAction;
import com.medical.audit.enums.AuditSeverity;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class CreateAuditLogRequest {

    @NotBlank
    private String actor;

    private String actorRole;

    @NotBlank
    private String serviceName;

    @NotNull
    private AuditAction action;

    private AuditSeverity severity;

    @NotBlank
    private String entityType;

    private Long entityId;

    private String details;

    private String oldValue;

    private String newValue;

    private String ipAddress;
}
