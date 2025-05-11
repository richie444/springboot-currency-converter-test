package com.example.rateservice.service;

import com.example.rateservice.model.ExchangeRateResponse;
import com.example.rateservice.model.RateResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.math.BigDecimal;

@Service
public class ExchangeRateService {

    private final WebClient webClient;
    private final String apiBaseUrl;
    private final String apiKey;

    public ExchangeRateService(
            WebClient webClient,
            @Value("${exchangerate.api.base-url:https://v6.exchangerate-api.com/v6}") String apiBaseUrl,
            @Value("${exchangerate.api.key:YOUR_API_KEY}") String apiKey) {
        this.webClient = webClient;
        this.apiBaseUrl = apiBaseUrl;
        this.apiKey = apiKey;
    }

    public RateResponse getExchangeRate(String fromCurrency, String toCurrency) {
        if (fromCurrency == null || toCurrency == null || fromCurrency.isEmpty() || toCurrency.isEmpty()) {
            throw new IllegalArgumentException("Currency codes cannot be null or empty");
        }

        String url = String.format("%s/%s/latest/%s", apiBaseUrl, apiKey, fromCurrency);
        
        ExchangeRateResponse response = webClient.get()
                .uri(url)
                .retrieve()
                .bodyToMono(ExchangeRateResponse.class)
                .block();

        if (response == null || response.getConversion_rates() == null) {
            throw new RuntimeException("Failed to retrieve exchange rates");
        }

        BigDecimal rate = response.getConversion_rates().get(toCurrency);
        if (rate == null) {
            throw new IllegalArgumentException("Invalid currency code: " + toCurrency);
        }

        return new RateResponse(fromCurrency, toCurrency, rate);
    }
}