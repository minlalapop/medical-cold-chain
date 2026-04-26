package com.medical.requisition.client;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

@Component
public class AuditClient {

    private final RestTemplate restTemplate = new RestTemplate();

    @Value("${audit.service.url}")
    private String auditServiceUrl;

    public void log(String actor, String action, Long entityId, String details) {
        try {
            restTemplate.postForObject(auditServiceUrl + "/api/audit-logs", Map.of(
                    "actor", actor,
                    "actorRole", "SYSTEM",
                    "serviceName", "requisition-service",
                    "action", action,
                    "severity", "INFO",
                    "entityType", "Requisition",
                    "entityId", entityId,
                    "details", details
            ), Void.class);
        } catch (Exception ignored) {
        }
    }
}
