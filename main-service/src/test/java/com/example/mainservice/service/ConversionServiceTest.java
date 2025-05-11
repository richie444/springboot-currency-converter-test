package com.example.mainservice.service;

import com.example.mainservice.model.Conversion;
import com.example.mainservice.model.ConversionRequest;
import com.example.mainservice.model.ConversionResponse;
import com.example.mainservice.model.RateResponse;
import com.example.mainservice.repository.ConversionRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;
import java.util.function.Function;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ConversionServiceTest {

    @Mock
    private WebClient webClient;
    
    @Mock
    private ConversionRepository conversionRepository;
    
    @InjectMocks
    private ConversionService conversionService;
    
    @Test
    void convertCurrency_ShouldCalculateAndSaveConversion() {
        // Prepare test data
        ConversionRequest request = new ConversionRequest();
        request.setFrom("USD");
        request.setTo("EUR");
        request.setAmount(new BigDecimal("100.00"));

        RateResponse rateResponse = new RateResponse();
        rateResponse.setFrom("USD");
        rateResponse.setTo("EUR");
        rateResponse.setRate(new BigDecimal("0.85"));

        // Configure mock WebClient chain
        WebClient.RequestHeadersUriSpec uriSpec = mock(WebClient.RequestHeadersUriSpec.class);
        WebClient.RequestHeadersSpec headersSpec = mock(WebClient.RequestHeadersSpec.class);
        WebClient.ResponseSpec responseSpec = mock(WebClient.ResponseSpec.class);

        when(webClient.get()).thenReturn(uriSpec);
        when(uriSpec.uri(any(Function.class))).thenReturn(headersSpec);
        when(headersSpec.retrieve()).thenReturn(responseSpec);
        when(responseSpec.bodyToMono(RateResponse.class)).thenReturn(Mono.just(rateResponse));

        // Configure mock repository
        when(conversionRepository.save(any(Conversion.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // Execute test
        ConversionResponse response = conversionService.convertCurrency(request);

        // Verify results
        assertNotNull(response);
        assertEquals("USD", response.getFrom());
        assertEquals("EUR", response.getTo());
        assertEquals(new BigDecimal("100.00"), response.getAmount());
        assertEquals(new BigDecimal("85.00"), response.getConvertedAmount());
        assertEquals(new BigDecimal("0.85"), response.getRate());

        // Verify conversion was saved
        ArgumentCaptor<Conversion> conversionCaptor = ArgumentCaptor.forClass(Conversion.class);
        verify(conversionRepository).save(conversionCaptor.capture());
        
        Conversion savedConversion = conversionCaptor.getValue();
        assertEquals("USD", savedConversion.getFromCurrency());
        assertEquals("EUR", savedConversion.getToCurrency());
        assertEquals(new BigDecimal("100.00"), savedConversion.getAmount());
        assertEquals(new BigDecimal("85.00"), savedConversion.getConvertedAmount());
        assertEquals(new BigDecimal("0.85"), savedConversion.getRate());
    }
}