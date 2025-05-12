package com.example.mainservice.service;

import com.example.mainservice.model.Conversion;
import com.example.mainservice.model.ConversionRequest;
import com.example.mainservice.model.ConversionResponse;
import com.example.mainservice.model.RateResponse;
import com.example.mainservice.repository.ConversionRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@Service
public class ConversionService {

    private final WebClient rateServiceClient;
    private final ConversionRepository conversionRepository;

    public ConversionService(
            WebClient rateServiceClient,
            ConversionRepository conversionRepository,
            @Value("${rate-service.base-url:http://rate-service:8081}") String rateServiceBaseUrl) {
        this.rateServiceClient = rateServiceClient;
        this.conversionRepository = conversionRepository;
    }

    public ConversionResponse convertCurrency(ConversionRequest request) {
        String fromCurrency = request.getFrom().toUpperCase();
        String toCurrency = request.getTo().toUpperCase();
        BigDecimal amount = request.getAmount();

        // Fetch exchange rate from rate-service
        RateResponse rateResponse = fetchExchangeRate(fromCurrency, toCurrency);

        // Calculate converted amount
        BigDecimal rate = rateResponse.getRate();
        BigDecimal convertedAmount = amount.multiply(rate).setScale(2, RoundingMode.HALF_UP);

        // Save conversion to database
        Conversion conversion = new Conversion(
                fromCurrency,
                toCurrency,
                amount,
                convertedAmount,
                rate
        );
        conversionRepository.save(conversion);

        // Return response
        return new ConversionResponse(
                fromCurrency,
                toCurrency,
                amount,
                convertedAmount,
                rate
        );
    }

    /**
     * Retrieves all stored currency conversions
     * @return List of all conversions
     */
    public List<Conversion> getAllConversions() {
        return StreamSupport
                .stream(conversionRepository.findAll().spliterator(), false)
                .collect(Collectors.toList());
    }

    private RateResponse fetchExchangeRate(String fromCurrency, String toCurrency) {
        return rateServiceClient
                .get()
                .uri(uriBuilder -> uriBuilder
                        .path("/rate")
                        .queryParam("from", fromCurrency)
                        .queryParam("to", toCurrency)
                        .build())
                .retrieve()
                .bodyToMono(RateResponse.class)
                .block();
    }
}