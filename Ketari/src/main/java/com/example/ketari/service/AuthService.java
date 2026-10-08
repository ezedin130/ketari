package com.example.ketari.service;

import com.example.ketari.dto.auth.AuthResponse;
import com.example.ketari.dto.auth.LoginRequest;
import com.example.ketari.dto.auth.RegisterRequest;
import com.example.ketari.dto.auth.TokenRefreshRequest;
import com.example.ketari.model.User;

/**
 * Service contract for user registration, authentication, and token management.
 */
public interface AuthService {

    AuthResponse register(RegisterRequest request);

    AuthResponse login(LoginRequest request);

    AuthResponse refreshToken(TokenRefreshRequest request);

    User getCurrentAuthenticatedUser();
}
