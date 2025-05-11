package com.example.mainservice.model;

import io.swagger.v3.oas.annotations.media.Schema;
import java.math.BigDecimal;

@Schema(description = "Response with currency conversion results")
public class ConversionResponse {
    
    @Schema(description = "Source currency code", example = "USD")
    private String from;
    
    @Schema(description = "Target currency code", example = "EUR")
    private String to;
    
    @Schema(description = "Original amount", example = "100.00")
    private BigDecimal amount;
    
    @Schema(description = "Converted amount", example = "85.00")
    private BigDecimal convertedAmount;
    
    @Schema(description = "Exchange rate used for conversion", example = "0.85")
    private BigDecimal rate;
    
    public ConversionResponse() {
    }
    
    public ConversionResponse(String from, String to, BigDecimal amount, BigDecimal convertedAmount, BigDecimal rate) {
        this.from = from;
        this.to = to;
        this.amount = amount;
        this.convertedAmount = convertedAmount;
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

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public BigDecimal getConvertedAmount() {
        return convertedAmount;
    }

    public void setConvertedAmount(BigDecimal convertedAmount) {
        this.convertedAmount = convertedAmount;
    }

    public BigDecimal getRate() {
        return rate;
    }

    public void setRate(BigDecimal rate) {
        this.rate = rate;
    }
}