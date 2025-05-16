package com.example.mainservice.service;

import com.example.mainservice.model.ConversionRequest;
import com.example.mainservice.model.RateResponse;
import com.example.mainservice.repository.ConversionRepository;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class ConversionServiceTest {
    @Test
    void convertCurrency_shouldReturnResponse() {
        WebClient webClient = mock(WebClient.class, RETURNS_DEEP_STUBS);
        ConversionRepository repo = mock(ConversionRepository.class);
        ConversionService service = new ConversionService(webClient, repo);
        RateResponse rateResponse = new RateResponse("USD", "EUR", new BigDecimal("0.85"));
        WebClient.RequestHeadersUriSpec uriSpec = mock(WebClient.RequestHeadersUriSpec.class);
        WebClient.RequestHeadersSpec headersSpec = mock(WebClient.RequestHeadersSpec.class);
        WebClient.ResponseSpec responseSpec = mock(WebClient.ResponseSpec.class);
        when(webClient.get()).thenReturn(uriSpec);
        when(uriSpec.uri(any())).thenReturn(headersSpec);
        when(headersSpec.retrieve()).thenReturn(responseSpec);
        when(responseSpec.bodyToMono(RateResponse.class)).thenReturn(Mono.just(rateResponse));
        when(repo.save(any())).thenAnswer(inv -> inv.getArgument(0));
        ConversionRequest req = new ConversionRequest("USD", "EUR", new BigDecimal("100"));
        var result = service.convertCurrency(req);
        assertEquals("USD", result.getFrom());
        assertEquals("EUR", result.getTo());
        assertEquals(new BigDecimal("85.00"), result.getConvertedAmount());
    }

    @Test
    void convertCurrency_shouldThrowOnApiError() {
        WebClient webClient = mock(WebClient.class, RETURNS_DEEP_STUBS);
        ConversionRepository repo = mock(ConversionRepository.class);
        ConversionService service = new ConversionService(webClient, repo);
        WebClient.RequestHeadersUriSpec uriSpec = mock(WebClient.RequestHeadersUriSpec.class);
        WebClient.RequestHeadersSpec headersSpec = mock(WebClient.RequestHeadersSpec.class);
        WebClient.ResponseSpec responseSpec = mock(WebClient.ResponseSpec.class);
        when(webClient.get()).thenReturn(uriSpec);
        when(uriSpec.uri(any())).thenReturn(headersSpec);
        when(headersSpec.retrieve()).thenReturn(responseSpec);
        when(responseSpec.bodyToMono(RateResponse.class)).thenReturn(Mono.error(new RuntimeException("API error")));
        ConversionRequest req = new ConversionRequest("USD", "EUR", new BigDecimal("100"));
        assertThrows(RuntimeException.class, () -> service.convertCurrency(req));
    }

    @Test
    void convertCurrency_shouldThrowOnNullRate() {
        WebClient webClient = mock(WebClient.class, RETURNS_DEEP_STUBS);
        ConversionRepository repo = mock(ConversionRepository.class);
        ConversionService service = new ConversionService(webClient, repo);
        WebClient.RequestHeadersUriSpec uriSpec = mock(WebClient.RequestHeadersUriSpec.class);
        WebClient.RequestHeadersSpec headersSpec = mock(WebClient.RequestHeadersSpec.class);
        WebClient.ResponseSpec responseSpec = mock(WebClient.ResponseSpec.class);
        when(webClient.get()).thenReturn(uriSpec);
        when(uriSpec.uri(any())).thenReturn(headersSpec);
        when(headersSpec.retrieve()).thenReturn(responseSpec);
        when(responseSpec.bodyToMono(RateResponse.class)).thenReturn(Mono.just(new RateResponse("USD", "EUR", null)));
        ConversionRequest req = new ConversionRequest("USD", "EUR", new BigDecimal("100"));
        assertThrows(RuntimeException.class, () -> service.convertCurrency(req));
    }
}
