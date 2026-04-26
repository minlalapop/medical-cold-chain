package com.medical.requisition.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI requisitionOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Requisition Service API")
                        .version("v1")
                        .description("API for creating, approving, rejecting, and delivering requisitions."));
    }
}
