package com.medical.audit.repository;

import com.medical.audit.entity.AuditLog;
import com.medical.audit.enums.AuditAction;
import com.medical.audit.enums.AuditSeverity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AuditLogRepository extends JpaRepository<AuditLog, Long> {

    List<AuditLog> findAllByOrderByTimestampDesc();

    List<AuditLog> findByServiceName(String serviceName);

    List<AuditLog> findByActor(String actor);

    List<AuditLog> findByEntityTypeAndEntityId(String entityType, Long entityId);

    List<AuditLog> findByAction(AuditAction action);

    List<AuditLog> findBySeverity(AuditSeverity severity);
}
