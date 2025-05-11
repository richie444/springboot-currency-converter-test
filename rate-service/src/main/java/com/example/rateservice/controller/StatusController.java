package com.example.rateservice.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@Tag(name = "Status", description = "Service health check endpoints")
public class StatusController {

    @Operation(
        summary = "Check service status", 
        description = "Returns the current status of the service"
    )
    @ApiResponse(
        responseCode = "200", 
        description = "Service is up and running",
        content = @Content(mediaType = "application/json", 
            schema = @Schema(type = "object", example = "{\"status\": \"UP\"}"))
    )
    @GetMapping("/status")
    public ResponseEntity<Map<String, String>> getStatus() {
        return ResponseEntity.ok(Map.of("status", "UP"));
    }
}