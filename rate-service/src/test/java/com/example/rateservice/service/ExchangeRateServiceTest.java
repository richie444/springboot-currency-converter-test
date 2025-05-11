package com.example.rateservice.service;

import com.example.rateservice.model.ExchangeRateResponse;
import com.example.rateservice.model.RateResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ExchangeRateServiceTest {

    @Mock
    private WebClient webClient;

    @InjectMocks
    private ExchangeRateService exchangeRateService;

    @Test
    void getExchangeRate_ShouldReturnRateResponse() {
        // Prepare mock response
        ExchangeRateResponse mockApiResponse = new ExchangeRateResponse();
        mockApiResponse.setBase_code("USD");
        Map<String, BigDecimal> rates = new HashMap<>();
        rates.put("EUR", new BigDecimal("0.85"));
        mockApiResponse.setConversion_rates(rates);

        // Configure mock WebClient chain with raw types to avoid generics issues
        WebClient.RequestHeadersUriSpec uriSpec = mock(WebClient.RequestHeadersUriSpec.class);
        WebClient.RequestHeadersSpec headersSpec = mock(WebClient.RequestHeadersSpec.class);
        WebClient.ResponseSpec responseSpec = mock(WebClient.ResponseSpec.class);

        when(webClient.get()).thenReturn(uriSpec);
        when(uriSpec.uri(anyString())).thenReturn(headersSpec);
        when(headersSpec.retrieve()).thenReturn(responseSpec);
        when(responseSpec.bodyToMono(ExchangeRateResponse.class)).thenReturn(Mono.just(mockApiResponse));

        // Inject API details via constructor instead of using mocked field
        exchangeRateService = new ExchangeRateService(webClient, "https://api.example.com", "test-key");

        // Perform test
        RateResponse result = exchangeRateService.getExchangeRate("USD", "EUR");

        // Verify results
        assertNotNull(result);
        assertEquals("USD", result.getFrom());
        assertEquals("EUR", result.getTo());
        assertEquals(new BigDecimal("0.85"), result.getRate());
    }

    @Test
    void getExchangeRate_WithNullCurrency_ShouldThrowException() {
        // Recreate service with test doubles
        exchangeRateService = new ExchangeRateService(webClient, "https://api.example.com", "test-key");

        assertThrows(IllegalArgumentException.class, () -> exchangeRateService.getExchangeRate(null, "EUR"));
        assertThrows(IllegalArgumentException.class, () -> exchangeRateService.getExchangeRate("USD", null));
    }

    @Test
    void getExchangeRate_WithInvalidTargetCurrency_ShouldThrowException() {
        // Prepare mock response
        ExchangeRateResponse mockApiResponse = new ExchangeRateResponse();
        mockApiResponse.setBase_code("USD");
        Map<String, BigDecimal> rates = new HashMap<>();
        rates.put("EUR", new BigDecimal("0.85"));
        mockApiResponse.setConversion_rates(rates);

        // Configure mock WebClient chain
        WebClient.RequestHeadersUriSpec uriSpec = mock(WebClient.RequestHeadersUriSpec.class);
        WebClient.RequestHeadersSpec headersSpec = mock(WebClient.RequestHeadersSpec.class);
        WebClient.ResponseSpec responseSpec = mock(WebClient.ResponseSpec.class);

        when(webClient.get()).thenReturn(uriSpec);
        when(uriSpec.uri(anyString())).thenReturn(headersSpec);
        when(headersSpec.retrieve()).thenReturn(responseSpec);
        when(responseSpec.bodyToMono(ExchangeRateResponse.class)).thenReturn(Mono.just(mockApiResponse));

        // Recreate service
        exchangeRateService = new ExchangeRateService(webClient, "https://api.example.com", "test-key");

        // Perform test & verify exception
        assertThrows(IllegalArgumentException.class, () -> exchangeRateService.getExchangeRate("USD", "INR"));
    }
}