package com.example.rateservice.controller;

import com.example.rateservice.model.RateResponse;
import com.example.rateservice.service.ExchangeRateService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@Validated
public class RateController {
    private static final Logger logger = LoggerFactory.getLogger(RateController.class);
    
    private final ExchangeRateService exchangeRateService;

    public RateController(ExchangeRateService exchangeRateService) {
        this.exchangeRateService = exchangeRateService;
    }

    @GetMapping("/rate")
    public ResponseEntity<?> getExchangeRate(
            @RequestParam String from,
            @RequestParam String to) {
        
        logger.info("Received request for exchange rate from {} to {}", from, to);
        
        // Validate currency codes
        if (!exchangeRateService.isValidCurrencyCode(from)) {
            logger.warn("Invalid source currency code: {}", from);
            return ResponseEntity.badRequest()
                    .body(Map.of("error", 
                          String.format("Invalid source currency code: %s. Must be a 3-letter ISO code.", from)));
        }

        if (!exchangeRateService.isValidCurrencyCode(to)) {
            logger.warn("Invalid target currency code: {}", to);
            return ResponseEntity.badRequest()
                    .body(Map.of("error", 
                          String.format("Invalid target currency code: %s. Must be a 3-letter ISO code.", to)));
        }
        
        try {
            RateResponse rateResponse = exchangeRateService.getExchangeRate(from.toUpperCase(), to.toUpperCase());
            return ResponseEntity.ok(rateResponse);
        } catch (Exception e) {
            logger.error("Error retrieving exchange rate", e);
            return ResponseEntity.internalServerError()
                    .body(Map.of("error", e.getMessage()));
        }
    }
}