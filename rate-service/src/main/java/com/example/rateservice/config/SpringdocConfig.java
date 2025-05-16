package com.example.rateservice.config;

import org.springdoc.core.models.GroupedOpenApi;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SpringdocConfig {
    
    @Bean
    public GroupedOpenApi mainEndpointsApi() {
        return GroupedOpenApi.builder()
                .group("Main Endpoints")
                .pathsToMatch("/status", "/rate")
                .build();
    }
}
