package com.medical.auth.client;

import com.medical.auth.dto.CreateUserRequest;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

@Component
public class UserClient {

    private final RestTemplate restTemplate = new RestTemplate();

    @Value("${user.service.url}")
    private String userServiceUrl;

    public void createUser(CreateUserRequest request) {
        try {
            restTemplate.postForObject(userServiceUrl + "/api/users", request, Void.class);
        } catch (HttpClientErrorException.BadRequest ignored) {
            // User already exists in user-service, so auth registration can continue.
        }
    }
}
