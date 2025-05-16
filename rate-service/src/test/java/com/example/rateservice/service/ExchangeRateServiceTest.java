package com.example.rateservice.service;

import com.example.rateservice.model.ExchangeRateResponse;
import com.example.rateservice.model.RateResponse;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@SuppressWarnings({"rawtypes", "unchecked"})
class ExchangeRateServiceTest {
    @Test
    void getExchangeRate_shouldReturnRate() {
        WebClient webClient = mock(WebClient.class, RETURNS_DEEP_STUBS);
        ExchangeRateService service = new ExchangeRateService(webClient, "http://fake", "key");
        ExchangeRateResponse apiResponse = new ExchangeRateResponse();
        apiResponse.setBase_code("USD");
        Map<String, BigDecimal> rates = new HashMap<>();
        rates.put("EUR", new BigDecimal("0.85"));
        apiResponse.setConversion_rates(rates);
        WebClient.RequestHeadersUriSpec uriSpec = mock(WebClient.RequestHeadersUriSpec.class);
        WebClient.RequestHeadersSpec headersSpec = mock(WebClient.RequestHeadersSpec.class);
        WebClient.ResponseSpec responseSpec = mock(WebClient.ResponseSpec.class);
        when(webClient.get()).thenReturn(uriSpec);
        when(uriSpec.uri(anyString())).thenReturn(headersSpec);
        when(headersSpec.retrieve()).thenReturn(responseSpec);
        when(responseSpec.bodyToMono(ExchangeRateResponse.class)).thenReturn(Mono.just(apiResponse));
        RateResponse result = service.getExchangeRate("USD", "EUR");
        assertEquals("USD", result.getFrom());
        assertEquals("EUR", result.getTo());
        assertEquals(new BigDecimal("0.85"), result.getRate());
    }

    @Test
    void getExchangeRate_shouldThrowOnNullCurrency() {
        ExchangeRateService service = new ExchangeRateService(mock(WebClient.class), "http://fake", "key");
        assertThrows(IllegalArgumentException.class, () -> service.getExchangeRate(null, "EUR"));
        assertThrows(IllegalArgumentException.class, () -> service.getExchangeRate("USD", null));
    }
}