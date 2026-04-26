package com.medical.requisition.client;

import com.medical.requisition.dto.AlertResponse;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Component
@RequiredArgsConstructor
public class AlertClient {

    private final RestTemplate restTemplate = new RestTemplate();

    @Value("${alert.service.url}")
    private String alertServiceUrl;

    public void refreshAlerts() {
        try {
            restTemplate.postForObject(alertServiceUrl + "/api/alerts/check-all", null, Void.class);
        } catch (Exception ignored) {
        }
    }

    public List<AlertResponse> getOpenAlerts() {
        try {
            return restTemplate.exchange(
                    alertServiceUrl + "/api/alerts/open",
                    HttpMethod.GET,
                    null,
                    new ParameterizedTypeReference<List<AlertResponse>>() {}
            ).getBody();
        } catch (Exception ignored) {
            return List.of();
        }
    }
}
