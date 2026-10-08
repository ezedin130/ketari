package com.example.ketari.exception;

import org.springframework.http.HttpStatus;

/**
 * Exception thrown when a resource already exists or violates a uniqueness invariant (HTTP 409).
 */
public class DuplicateResourceException extends ApiException {

    public DuplicateResourceException(String message) {
        super(HttpStatus.CONFLICT, "DUPLICATE_RESOURCE", message);
    }
}
