package com.example.ketari.controller;

import com.example.ketari.dto.ApiResponse;
import com.example.ketari.dto.admin.HealthResponse;
import com.example.ketari.service.HealthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Controller providing system liveliness and database connectivity health check.
 */
@Tag(name = "Health", description = "Public health check endpoint for monitoring and uptime probes")
@RestController
@RequestMapping("/api/health")
@RequiredArgsConstructor
public class HealthController {

    private final HealthService healthService;

    @Operation(summary = "Check application and database health status")
    @GetMapping
    public ApiResponse<HealthResponse> checkHealth() {
        HealthResponse response = this.healthService.checkHealth();
        return ApiResponse.success("Health check completed", response);
    }
}
