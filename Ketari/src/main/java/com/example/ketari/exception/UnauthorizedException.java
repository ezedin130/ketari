package com.example.ketari.exception;

import org.springframework.http.HttpStatus;

/**
 * Exception representing unauthenticated or invalid credential requests (HTTP 401).
 */
public class UnauthorizedException extends ApiException {

    public UnauthorizedException(String message) {
        super(HttpStatus.UNAUTHORIZED, "UNAUTHORIZED", message);
    }
}
