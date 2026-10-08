package com.example.ketari.exception;

import org.springframework.http.HttpStatus;

/**
 * Exception thrown when a requested domain resource cannot be found (HTTP 404).
 */
public class ResourceNotFoundException extends ApiException {

    public ResourceNotFoundException(String message) {
        super(HttpStatus.NOT_FOUND, "RESOURCE_NOT_FOUND", message);
    }

    public ResourceNotFoundException(String resourceName, String fieldName, Object fieldValue) {
        super(HttpStatus.NOT_FOUND, "RESOURCE_NOT_FOUND",
                String.format("%s not found with %s: '%s'", resourceName, fieldName, fieldValue));
    }
}
