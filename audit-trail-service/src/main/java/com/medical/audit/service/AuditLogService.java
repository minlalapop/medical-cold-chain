package com.medical.audit.service;

import com.medical.audit.dto.CreateAuditLogRequest;
import com.medical.audit.entity.AuditLog;
import com.medical.audit.enums.AuditAction;
import com.medical.audit.enums.AuditSeverity;
import com.medical.audit.repository.AuditLogRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AuditLogService {

    private final AuditLogRepository auditLogRepository;

    public AuditLog createLog(CreateAuditLogRequest request) {
        AuditLog log = AuditLog.builder()
                .actor(request.getActor())
                .actorRole(request.getActorRole())
                .serviceName(request.getServiceName())
                .action(request.getAction())
                .severity(request.getSeverity())
                .entityType(request.getEntityType())
                .entityId(request.getEntityId())
                .details(request.getDetails())
                .oldValue(request.getOldValue())
                .newValue(request.getNewValue())
                .ipAddress(request.getIpAddress())
                .build();

        return auditLogRepository.save(log);
    }

    public List<AuditLog> getAllLogs() {
        return auditLogRepository.findAllByOrderByTimestampDesc();
    }

    public List<AuditLog> getLogsByService(String serviceName) {
        return auditLogRepository.findByServiceName(serviceName);
    }

    public List<AuditLog> getLogsByActor(String actor) {
        return auditLogRepository.findByActor(actor);
    }

    public List<AuditLog> getLogsByEntity(String entityType, Long entityId) {
        return auditLogRepository.findByEntityTypeAndEntityId(entityType, entityId);
    }

    public List<AuditLog> getLogsByAction(AuditAction action) {
        return auditLogRepository.findByAction(action);
    }

    public List<AuditLog> getLogsBySeverity(AuditSeverity severity) {
        return auditLogRepository.findBySeverity(severity);
    }
}
