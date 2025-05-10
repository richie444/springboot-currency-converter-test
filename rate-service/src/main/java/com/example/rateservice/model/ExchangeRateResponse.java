package com.example.rateservice.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.Map;

public class ExchangeRateResponse {
    @JsonProperty("base_code")
    private String baseCode;
    
    @JsonProperty("conversion_rates")
    private Map<String, Double> conversionRates;

    public ExchangeRateResponse() {
    }

    public String getBaseCode() {
        return baseCode;
    }

    public void setBaseCode(String baseCode) {
        this.baseCode = baseCode;
    }

    public Map<String, Double> getConversionRates() {
        return conversionRates;
    }

    public void setConversionRates(Map<String, Double> conversionRates) {
        this.conversionRates = conversionRates;
    }
}