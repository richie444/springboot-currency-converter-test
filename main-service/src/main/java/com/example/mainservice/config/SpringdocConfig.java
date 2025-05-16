package com.example.mainservice.config;

import org.springdoc.core.models.GroupedOpenApi;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SpringdocConfig {
    
    @Bean
    public GroupedOpenApi mainEndpointsApi() {
        return GroupedOpenApi.builder()
                .group("Main Endpoints")
                .pathsToMatch("/api/auth/signup", "/api/auth/login", "/convert", "/conversions", "/status")
                .build();
    }
}
