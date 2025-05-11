package com.example.rateservice.model;

import io.swagger.v3.oas.annotations.media.Schema;
import java.math.BigDecimal;

@Schema(description = "Response containing currency exchange rate information")
public class RateResponse {
    
    @Schema(description = "Source currency code", example = "USD")
    private String from;
    
    @Schema(description = "Target currency code", example = "EUR")
    private String to;
    
    @Schema(description = "Exchange rate value", example = "0.85")
    private BigDecimal rate;

    public RateResponse(String from, String to, BigDecimal rate) {
        this.from = from;
        this.to = to;
        this.rate = rate;
    }

    public String getFrom() {
        return from;
    }

    public void setFrom(String from) {
        this.from = from;
    }

    public String getTo() {
        return to;
    }

    public void setTo(String to) {
        this.to = to;
    }

    public BigDecimal getRate() {
        return rate;
    }

    public void setRate(BigDecimal rate) {
        this.rate = rate;
    }
}