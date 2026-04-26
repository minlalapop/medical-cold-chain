package com.medical.alert.client;

import com.medical.alert.dto.TemperatureIncidentResponse;
import java.util.List;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Component
public class TemperatureClient {

    private final RestTemplate restTemplate = new RestTemplate();

    @Value("${temperature.service.url}")
    private String temperatureServiceUrl;

    public List<TemperatureIncidentResponse> getOpenTemperatureIncidents() {
        String url = temperatureServiceUrl + "/api/temperatures/incidents/open";

        return restTemplate.exchange(
                url,
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<List<TemperatureIncidentResponse>>() {}
        ).getBody();
    }
}
