package com.example.ketari.controller;

import com.example.ketari.dto.ApiResponse;
import com.example.ketari.dto.auth.AuthResponse;
import com.example.ketari.dto.auth.LoginRequest;
import com.example.ketari.dto.auth.RegisterRequest;
import com.example.ketari.dto.auth.TokenRefreshRequest;
import com.example.ketari.dto.user.UserResponse;
import com.example.ketari.mapper.UserMapper;
import com.example.ketari.model.User;
import com.example.ketari.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

/**
 * Controller handling user authentication, registration, session checks, and token refreshes.
 */
@Tag(name = "Authentication", description = "Endpoints for user registration, login, and token management")
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @Operation(summary = "Register a new user account")
    @PostMapping("/register")
    @ResponseStatus(HttpStatus.CREATED)
    public ApiResponse<AuthResponse> register(@Valid @RequestBody RegisterRequest request) {
        AuthResponse response = this.authService.register(request);
        return ApiResponse.success("User registered successfully", response);
    }

    @Operation(summary = "Authenticate user credentials and obtain JWT tokens")
    @PostMapping("/login")
    public ApiResponse<AuthResponse> login(@Valid @RequestBody LoginRequest request) {
        AuthResponse response = this.authService.login(request);
        return ApiResponse.success("Login successful", response);
    }

    @Operation(summary = "Refresh expired access token using stateless refresh token")
    @PostMapping("/refresh")
    public ApiResponse<AuthResponse> refresh(@Valid @RequestBody TokenRefreshRequest request) {
        AuthResponse response = this.authService.refreshToken(request);
        return ApiResponse.success("Token refreshed successfully", response);
    }

    @Operation(summary = "Retrieve currently authenticated user profile")
    @GetMapping("/me")
    public ApiResponse<UserResponse> getCurrentUser() {
        User user = this.authService.getCurrentAuthenticatedUser();
        return ApiResponse.success("Authenticated user retrieved", UserMapper.toResponse(user));
    }
}
