package com.medical.alert.client;

import com.medical.alert.dto.BatchResponse;
import java.util.List;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Component
public class InventoryClient {

    private final RestTemplate restTemplate = new RestTemplate();

    @Value("${inventory.service.url}")
    private String inventoryServiceUrl;

    public List<BatchResponse> getAllBatches() {
        String url = inventoryServiceUrl + "/api/batches";

        return restTemplate.exchange(
                url,
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<List<BatchResponse>>() {}
        ).getBody();
    }
}
