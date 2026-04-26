package com.medical.user.client;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponentsBuilder;
import org.springframework.web.client.RestTemplate;

@Component
@RequiredArgsConstructor
public class AuthClient {

    private final RestTemplate restTemplate = new RestTemplate();

    @Value("${auth.service.url}")
    private String authServiceUrl;

    public void deleteAuthUser(String email) {
        String url = UriComponentsBuilder
                .fromUriString(authServiceUrl)
                .pathSegment("api", "auth", "internal", "users", email)
                .toUriString();

        restTemplate.delete(url);
    }
}
