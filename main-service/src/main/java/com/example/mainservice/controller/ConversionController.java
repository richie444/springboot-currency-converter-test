package com.example.mainservice.controller;

import com.example.mainservice.model.ConversionRequest;
import com.example.mainservice.model.ConversionResponse;
import com.example.mainservice.service.ConversionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Tag(name = "Currency Conversion", description = "Currency conversion operations")
public class ConversionController {

    private final ConversionService conversionService;

    public ConversionController(ConversionService conversionService) {
        this.conversionService = conversionService;
    }

    @Operation(summary = "Convert currency", description = "Converts an amount from one currency to another using current exchange rates")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Conversion successful", 
                    content = @Content(mediaType = "application/json", 
                    schema = @Schema(implementation = ConversionResponse.class))),
        @ApiResponse(responseCode = "400", description = "Invalid request"),
        @ApiResponse(responseCode = "401", description = "Unauthorized"),
        @ApiResponse(responseCode = "503", description = "Rate service unavailable")
    })
    @PostMapping("/convert")
    public ResponseEntity<ConversionResponse> convertCurrency(@Valid @RequestBody ConversionRequest request) {
        ConversionResponse response = conversionService.convertCurrency(request);
        return ResponseEntity.ok(response);
    }
}