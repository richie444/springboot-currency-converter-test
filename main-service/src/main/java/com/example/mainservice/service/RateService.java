package com.example.mainservice.service;

import com.example.mainservice.model.RateResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;

@Service
public class RateService {
    private static final Logger logger = LoggerFactory.getLogger(RateService.class);
    
    private final WebClient webClient;

    public RateService(
            WebClient.Builder webClientBuilder,
            @Value("${rate-service.base-url}") String baseUrl) {
        
        this.webClient = webClientBuilder.baseUrl(baseUrl).build();
        logger.info("RateService initialized with baseUrl: {}", baseUrl);
    }

    public RateResponse getExchangeRate(String from, String to) {
        logger.info("Getting exchange rate from {} to {} from rate-service", from, to);
        
        try {
            return webClient
                    .get()
                    .uri(uriBuilder -> uriBuilder
                            .path("/rate")
                            .queryParam("from", from)
                            .queryParam("to", to)
                            .build())
                    .retrieve()
                    .bodyToMono(RateResponse.class)
                    .block();
        } catch (WebClientResponseException e) {
            logger.error("Error calling rate-service: status {}, body: {}", e.getStatusCode(), e.getResponseBodyAsString());
            throw new RuntimeException("Error retrieving exchange rate from rate service: " + e.getMessage(), e);
        } catch (Exception e) {
            logger.error("Error calling rate-service", e);
            throw new RuntimeException("Error retrieving exchange rate from rate service: " + e.getMessage(), e);
        }
    }
}