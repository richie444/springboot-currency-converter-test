package com.example.rateservice.model;

import java.math.BigDecimal;

public class RateResponse {
    private String from;
    private String to;
    private BigDecimal rate;

    public RateResponse() {
    }

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