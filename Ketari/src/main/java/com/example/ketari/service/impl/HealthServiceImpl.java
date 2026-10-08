package com.example.ketari.service.impl;

import com.example.ketari.dto.admin.HealthResponse;
import com.example.ketari.service.HealthService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.sql.DataSource;
import java.sql.Connection;
import java.time.LocalDateTime;

/**
 * Implementation of HealthService verifying database liveliness.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class HealthServiceImpl implements HealthService {

    private final DataSource dataSource;

    @Override
    public HealthResponse checkHealth() {
        boolean dbConnected = false;
        try (Connection connection = this.dataSource.getConnection()) {
            dbConnected = connection.isValid(2);
        } catch (Exception e) {
            log.error("Database health check failed: {}", e.getMessage());
        }

        return HealthResponse.builder()
                .status(dbConnected ? "UP" : "DOWN")
                .database(dbConnected ? "CONNECTED" : "DISCONNECTED")
                .timestamp(LocalDateTime.now())
                .build();
    }
}
