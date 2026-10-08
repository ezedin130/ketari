package com.example.ketari.security;

import com.example.ketari.model.User;
import com.example.ketari.utils.Constants;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

/**
 * JJWT-based implementation of JwtService for stateless access and refresh tokens.
 */
@Slf4j
@Service
public class JwtServiceImpl implements JwtService {

    @Value("${app.jwt.secret:404E635266556A586E3272357538782F413F4428472B4B6250645367566B5970}")
    private String jwtSecret;

    @Value("${app.jwt.expiration-ms:86400000}")
    private long jwtExpirationMs; // 24 hours

    @Value("${app.jwt.refresh-expiration-ms:604800000}")
    private long refreshExpirationMs; // 7 days

    @Override
    public String generateAccessToken(User user) {
        return generateAccessToken(new HashMap<>(), user);
    }

    @Override
    public String generateAccessToken(Map<String, Object> extraClaims, User user) {
        Map<String, Object> claims = new HashMap<>(extraClaims);
        claims.put("userId", user.getId());
        claims.put("role", user.getRole().name());
        claims.put(Constants.TOKEN_TYPE_CLAIM, Constants.ACCESS_TOKEN_TYPE);

        return buildToken(claims, user.getUsername(), this.jwtExpirationMs);
    }

    @Override
    public String generateRefreshToken(User user) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("userId", user.getId());
        claims.put(Constants.TOKEN_TYPE_CLAIM, Constants.REFRESH_TOKEN_TYPE);

        return buildToken(claims, user.getUsername(), this.refreshExpirationMs);
    }

    private String buildToken(Map<String, Object> claims, String subject, long expirationMs) {
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + expirationMs);

        return Jwts.builder()
                .setClaims(claims)
                .setSubject(subject)
                .setIssuedAt(now)
                .setExpiration(expiryDate)
                .signWith(getSigningKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    @Override
    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    @Override
    public String extractRole(String token) {
        Claims claims = extractAllClaims(token);
        return claims != null ? (String) claims.get("role") : null;
    }

    @Override
    public Long extractUserId(String token) {
        Claims claims = extractAllClaims(token);
        if (claims != null && claims.get("userId") != null) {
            Object userIdObj = claims.get("userId");
            if (userIdObj instanceof Number number) {
                return number.longValue();
            }
        }
        return null;
    }

    @Override
    public String extractTokenType(String token) {
        Claims claims = extractAllClaims(token);
        return claims != null ? (String) claims.get(Constants.TOKEN_TYPE_CLAIM) : null;
    }

    @Override
    public boolean isTokenValid(String token, UserDetails userDetails) {
        try {
            final String username = extractUsername(token);
            return username != null
                    && username.equalsIgnoreCase(userDetails.getUsername())
                    && !isTokenExpired(token)
                    && Constants.ACCESS_TOKEN_TYPE.equals(extractTokenType(token));
        } catch (JwtException | IllegalArgumentException e) {
            log.debug("Token validation failed: {}", e.getMessage());
            return false;
        }
    }

    @Override
    public boolean isRefreshToken(String token) {
        try {
            return !isTokenExpired(token) && Constants.REFRESH_TOKEN_TYPE.equals(extractTokenType(token));
        } catch (JwtException | IllegalArgumentException e) {
            return false;
        }
    }

    @Override
    public long getAccessTokenExpirationMs() {
        return this.jwtExpirationMs;
    }

    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claims != null ? claimsResolver.apply(claims) : null;
    }

    private Claims extractAllClaims(String token) {
        try {
            return Jwts.parserBuilder()
                    .setSigningKey(getSigningKey())
                    .build()
                    .parseClaimsJws(token)
                    .getBody();
        } catch (JwtException | IllegalArgumentException e) {
            log.debug("Failed to extract claims from token: {}", e.getMessage());
            return null;
        }
    }

    private boolean isTokenExpired(String token) {
        Date expiration = extractClaim(token, Claims::getExpiration);
        return expiration != null && expiration.before(new Date());
    }

    private Key getSigningKey() {
        byte[] keyBytes = this.jwtSecret.getBytes(StandardCharsets.UTF_8);
        return Keys.hmacShaKeyFor(keyBytes);
    }
}
