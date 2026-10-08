package com.example.ketari.exception;

import org.springframework.http.HttpStatus;

import java.util.List;

/**
 * Exception representing bad or malformed client requests (HTTP 400).
 */
public class BadRequestException extends ApiException {

    public BadRequestException(String message) {
        super(HttpStatus.BAD_REQUEST, "BAD_REQUEST", message);
    }

    public BadRequestException(String message, List<String> details) {
        super(HttpStatus.BAD_REQUEST, "BAD_REQUEST", message, details);
    }
}
