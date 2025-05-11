package com.example.mainservice.model;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;

@Schema(description = "Request to convert an amount between two currencies")
public class ConversionRequest {
    
    @Schema(description = "Source currency code (3 letters)", example = "USD", required = true)
    @NotBlank(message = "Source currency is required")
    private String from;
    
    @Schema(description = "Target currency code (3 letters)", example = "EUR", required = true)
    @NotBlank(message = "Target currency is required")
    private String to;
    
    @Schema(description = "Amount to convert", example = "100.00", required = true)
    @NotNull(message = "Amount is required")
    @Positive(message = "Amount must be positive")
    private BigDecimal amount;

    // Getters and setters
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
}