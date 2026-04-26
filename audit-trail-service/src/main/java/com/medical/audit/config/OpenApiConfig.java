package com.medical.audit.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI auditTrailServiceOpenApi() {
        return new OpenAPI()
                .info(new Info()
                        .title("Audit Trail Service API")
                        .description("Immutable audit log API for medical cold chain traceability.")
                        .version("v1"));
    }
}
