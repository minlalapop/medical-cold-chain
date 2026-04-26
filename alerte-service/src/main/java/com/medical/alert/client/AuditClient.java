package com.medical.alert.client;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;

@Component
public class AuditClient {

    private final RestTemplate restTemplate = new RestTemplate();

    @Value("${audit.service.url}")
    private String auditServiceUrl;

    public void log(String actor, String action, String entityType, Long entityId, String details) {
        try {
            Map<String, Object> body = new HashMap<>();
            body.put("actor", actor);
            body.put("actorRole", "SYSTEM");
            body.put("serviceName", "alert-service");
            body.put("action", action);
            body.put("severity", "INFO");
            body.put("entityType", entityType);
            body.put("entityId", entityId);
            body.put("details", details);

            restTemplate.postForObject(auditServiceUrl + "/api/audit-logs", body, Void.class);
        } catch (Exception ignored) {
        }
    }
}
