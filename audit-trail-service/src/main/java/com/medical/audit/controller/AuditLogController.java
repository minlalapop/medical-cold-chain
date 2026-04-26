package com.medical.audit.controller;

import com.medical.audit.dto.CreateAuditLogRequest;
import com.medical.audit.entity.AuditLog;
import com.medical.audit.enums.AuditAction;
import com.medical.audit.enums.AuditSeverity;
import com.medical.audit.service.AuditLogService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/audit-logs")
@RequiredArgsConstructor
public class AuditLogController {

    private final AuditLogService auditLogService;

    @PostMapping
    public AuditLog createLog(@Valid @RequestBody CreateAuditLogRequest request) {
        return auditLogService.createLog(request);
    }

    @GetMapping
    public List<AuditLog> getAllLogs() {
        return auditLogService.getAllLogs();
    }

    @GetMapping("/service/{serviceName}")
    public List<AuditLog> getLogsByService(@PathVariable String serviceName) {
        return auditLogService.getLogsByService(serviceName);
    }

    @GetMapping("/actor/{actor}")
    public List<AuditLog> getLogsByActor(@PathVariable String actor) {
        return auditLogService.getLogsByActor(actor);
    }

    @GetMapping("/entity/{entityType}/{entityId}")
    public List<AuditLog> getLogsByEntity(@PathVariable String entityType,
                                          @PathVariable Long entityId) {
        return auditLogService.getLogsByEntity(entityType, entityId);
    }

    @GetMapping("/action/{action}")
    public List<AuditLog> getLogsByAction(@PathVariable AuditAction action) {
        return auditLogService.getLogsByAction(action);
    }

    @GetMapping("/severity/{severity}")
    public List<AuditLog> getLogsBySeverity(@PathVariable AuditSeverity severity) {
        return auditLogService.getLogsBySeverity(severity);
    }
}
