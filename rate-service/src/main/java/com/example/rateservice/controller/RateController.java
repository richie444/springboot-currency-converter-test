package com.example.rateservice.controller;

import com.example.rateservice.model.RateResponse;
import com.example.rateservice.service.ExchangeRateService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.NotBlank;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Validated
@Tag(name = "Exchange Rates", description = "APIs for retrieving currency exchange rates")
public class RateController {

    private final ExchangeRateService exchangeRateService;

    public RateController(ExchangeRateService exchangeRateService) {
        this.exchangeRateService = exchangeRateService;
    }

    @Operation(
        summary = "Get exchange rate", 
        description = "Retrieves the current exchange rate between two currencies"
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200", 
            description = "Exchange rate retrieved successfully",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = RateResponse.class))
        ),
        @ApiResponse(responseCode = "400", description = "Invalid currency codes"),
        @ApiResponse(responseCode = "401", description = "Unauthorized"),
        @ApiResponse(responseCode = "503", description = "Exchange rate API unavailable")
    })
    @GetMapping("/rate")
    public ResponseEntity<RateResponse> getExchangeRate(
            @Parameter(description = "Source currency code (3 letters)", example = "USD", required = true)
            @RequestParam @NotBlank String from,
            
            @Parameter(description = "Target currency code (3 letters)", example = "EUR", required = true)
            @RequestParam @NotBlank String to) {
        
        RateResponse rateResponse = exchangeRateService.getExchangeRate(from.toUpperCase(), to.toUpperCase());
        return ResponseEntity.ok(rateResponse);
    }
}