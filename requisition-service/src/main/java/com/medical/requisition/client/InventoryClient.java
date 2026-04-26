package com.medical.requisition.client;

import com.medical.requisition.dto.ConsumeStockRequest;
import com.medical.requisition.dto.StockResponse;
import com.medical.requisition.exception.BadRequestException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

@Component
public class InventoryClient {

    private final RestTemplate restTemplate = new RestTemplate();

    @Value("${inventory.service.url}")
    private String inventoryServiceUrl;

    public boolean checkStock(Long productId, int quantity) {
        String url = UriComponentsBuilder.fromUriString(inventoryServiceUrl)
                .path("/api/inventory/check")
                .queryParam("productId", productId)
                .queryParam("quantity", quantity)
                .toUriString();

        return Boolean.TRUE.equals(restTemplate.getForObject(url, Boolean.class));
    }

    public void consumeStock(Long productId, int quantity, String reason) {
        String url = inventoryServiceUrl + "/api/inventory/consume";

        ConsumeStockRequest request = new ConsumeStockRequest(
                productId,
                quantity,
                reason
        );

        try {
            restTemplate.postForObject(url, request, Void.class);
        } catch (HttpStatusCodeException ex) {
            throw new BadRequestException(extractErrorMessage(ex.getResponseBodyAsString()));
        }
    }

    public StockResponse getStock(Long productId) {
        String url = inventoryServiceUrl + "/api/inventory/stock/" + productId;
        return restTemplate.getForObject(url, StockResponse.class);
    }

    private String extractErrorMessage(String responseBody) {
        if (responseBody == null || responseBody.isBlank()) {
            return "Inventory service rejected the operation";
        }

        int marker = responseBody.indexOf("\"error\":\"");
        if (marker < 0) {
            return responseBody;
        }

        int start = marker + 9;
        int end = responseBody.indexOf('"', start);

        if (end < 0) {
            return responseBody;
        }

        return responseBody.substring(start, end);
    }
}
