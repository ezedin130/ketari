package com.example.ketari.service;

import com.example.ketari.dto.admin.HealthResponse;

/**
 * Service contract for system health and connectivity checks.
 */
public interface HealthService {

    HealthResponse checkHealth();
}
