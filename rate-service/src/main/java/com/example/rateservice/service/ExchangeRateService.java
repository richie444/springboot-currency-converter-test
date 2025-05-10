package com.example.rateservice.service;

import com.example.rateservice.model.ExchangeRateResponse;
import com.example.rateservice.model.RateResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.math.BigDecimal;
import java.math.RoundingMode;

@Service
public class ExchangeRateService {
    private static final Logger logger = LoggerFactory.getLogger(ExchangeRateService.class);
    
    private final WebClient webClient;
    private final String apiKey;

    public ExchangeRateService(
            WebClient.Builder webClientBuilder,
            @Value("${exchangerate.api.base-url}") String baseUrl,
            @Value("${exchangerate.api.key}") String apiKey) {
        
        this.webClient = webClientBuilder.baseUrl(baseUrl).build();
        this.apiKey = apiKey;
        logger.info("ExchangeRateService initialized with baseUrl: {}", baseUrl);
    }

    public RateResponse getExchangeRate(String from, String to) {
        logger.info("Getting exchange rate from {} to {}", from, to);

        // Make a call to the ExchangeRate-API
        String path = "/" + apiKey + "/latest/" + from;
        
        ExchangeRateResponse response = webClient
                .get()
                .uri(path)
                .retrieve()
                .bodyToMono(ExchangeRateResponse.class)
                .block();

        if (response == null || response.getConversionRates() == null) {
            logger.error("Null or invalid response from exchange rate API");
            throw new RuntimeException("Failed to retrieve exchange rate");
        }

        // Get the conversion rate for the target currency
        Double rate = response.getConversionRates().get(to);
        if (rate == null) {
            logger.error("Conversion rate not found for target currency: {}", to);
            throw new RuntimeException("Conversion rate not found for target currency: " + to);
        }

        logger.info("Exchange rate from {} to {} is {}", from, to, rate);
        return new RateResponse(from, to, BigDecimal.valueOf(rate).setScale(6, RoundingMode.HALF_UP));
    }

    public boolean isValidCurrencyCode(String code) {
        return code != null && code.matches("[A-Z]{3}");
    }
}