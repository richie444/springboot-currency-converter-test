package com.example.mainservice.controller;

import com.example.mainservice.model.Conversion;
import com.example.mainservice.model.ConversionRequest;
import com.example.mainservice.model.ConversionResponse;
import com.example.mainservice.service.ConversionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

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

    @Operation(summary = "Get all conversions", description = "Retrieves all stored currency conversion records")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Conversions retrieved successfully", 
                    content = @Content(mediaType = "application/json", 
                    array = @ArraySchema(schema = @Schema(implementation = Conversion.class)))),
        @ApiResponse(responseCode = "401", description = "Unauthorized"),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @GetMapping("/conversions")
    public ResponseEntity<List<Conversion>> getAllConversions() {
        List<Conversion> conversions = conversionService.getAllConversions();
        return ResponseEntity.ok(conversions);
    }
}