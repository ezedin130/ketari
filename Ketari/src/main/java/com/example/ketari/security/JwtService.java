package com.example.ketari.security;

import com.example.ketari.model.User;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Map;

/**
 * Service contract for stateless JWT token operations.
 */
public interface JwtService {

    String generateAccessToken(User user);

    String generateAccessToken(Map<String, Object> extraClaims, User user);

    String generateRefreshToken(User user);

    String extractUsername(String token);

    String extractRole(String token);

    Long extractUserId(String token);

    String extractTokenType(String token);

    boolean isTokenValid(String token, UserDetails userDetails);

    boolean isRefreshToken(String token);

    long getAccessTokenExpirationMs();
}
