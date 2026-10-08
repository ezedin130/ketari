package com.example.ketari.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

import java.util.ArrayList;
import java.util.List;

/**
 * Base abstract runtime exception for domain-level application errors.
 */
@Getter
public abstract class ApiException extends RuntimeException {

    private final HttpStatus status;
    private final String errorCode;
    private final List<String> details;

    protected ApiException(HttpStatus status, String errorCode, String message) {
        super(message);
        this.status = status;
        this.errorCode = errorCode;
        this.details = new ArrayList<>();
    }

    protected ApiException(HttpStatus status, String errorCode, String message, List<String> details) {
        super(message);
        this.status = status;
        this.errorCode = errorCode;
        this.details = details != null ? details : new ArrayList<>();
    }
}
