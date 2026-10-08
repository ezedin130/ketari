package com.example.ketari.utils;

/**
 * Global constant definitions for headers, MDC keys, security roles, and token types.
 */
public final class Constants {

    private Constants() {
        // Prevent instantiation
    }

    public static final String HEADER_CORRELATION_ID = "X-Correlation-ID";
    public static final String CORRELATION_ID_HEADER = "X-Correlation-ID";
    public static final String HEADER_REQUEST_ID = "X-Request-ID";
    public static final String REQUEST_ID_HEADER = "X-Request-ID";
    public static final String HEADER_AUTHORIZATION = "Authorization";
    public static final String BEARER_PREFIX = "Bearer ";

    public static final String MDC_CORRELATION_ID = "correlationId";
    public static final String CORRELATION_ID_MDC_KEY = "correlationId";
    public static final String MDC_REQUEST_ID = "requestId";
    public static final String REQUEST_ID_MDC_KEY = "requestId";

    public static final String ROLE_EMPLOYEE = "ROLE_EMPLOYEE";
    public static final String ROLE_EMPLOYER = "ROLE_EMPLOYER";
    public static final String ROLE_ADMIN = "ROLE_ADMIN";

    public static final String TOKEN_TYPE_CLAIM = "tokenType";
    public static final String ACCESS_TOKEN_TYPE = "ACCESS";
    public static final String REFRESH_TOKEN_TYPE = "REFRESH";
    public static final String TOKEN_TYPE_ACCESS = "ACCESS";
    public static final String TOKEN_TYPE_REFRESH = "REFRESH";
}
