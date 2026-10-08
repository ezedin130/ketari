package com.example.ketari.exception;

import org.springframework.http.HttpStatus;

/**
 * Exception representing file upload validation failures (format, MIME, size, path).
 */
public class FileValidationException extends ApiException {

    public FileValidationException(String message) {
        super(HttpStatus.BAD_REQUEST, "FILE_VALIDATION_ERROR", message);
    }
}
