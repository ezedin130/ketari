package com.example.ketari.config;

import com.example.ketari.dto.ErrorResponse;
import com.example.ketari.filter.CorrelationIdFilter;
import com.example.ketari.security.JwtAuthenticationFilter;
import com.example.ketari.utils.Constants;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.slf4j.MDC;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfigurationSource;

import java.time.LocalDateTime;

/**
 * Main Spring Security configuration.
 * Enforces stateless JWT authentication and role-based access control.
 */
@Configuration
@EnableWebSecurity
@EnableMethodSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthFilter;
    private final CorrelationIdFilter correlationIdFilter;
    private final AuthenticationProvider authenticationProvider;
    private final CorsConfigurationSource corsConfigurationSource;
    private final ObjectMapper objectMapper;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable)
                .cors(cors -> cors.configurationSource(this.corsConfigurationSource))
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authenticationProvider(this.authenticationProvider)
                .exceptionHandling(exceptions -> exceptions
                        .authenticationEntryPoint((request, response, authException) -> {
                            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                            response.setContentType(MediaType.APPLICATION_JSON_VALUE);

                            String correlationId = MDC.get(Constants.MDC_CORRELATION_ID);
                            ErrorResponse errorResponse = ErrorResponse.builder()
                                    .status(HttpServletResponse.SC_UNAUTHORIZED)
                                    .error("Unauthorized")
                                    .message(authException.getMessage() != null ? authException.getMessage() : "Full authentication is required to access this resource")
                                    .path(request.getRequestURI())
                                    .correlationId(correlationId)
                                    .timestamp(java.time.Instant.now().toString())
                                    .build();

                            this.objectMapper.writeValue(response.getOutputStream(), errorResponse);
                        })
                )
                .authorizeHttpRequests(auth -> auth
                        // Publicly accessible endpoints
                        .requestMatchers(
                                "/api/auth/**",
                                "/api/jobs/public/**",
                                "/api/health",
                                "/v3/api-docs/**",
                                "/swagger-ui/**",
                                "/swagger-ui.html",
                                "/ws/**"
                        ).permitAll()
                        // Admin restricted endpoints
                        .requestMatchers("/api/admin/**").hasRole("ADMIN")
                        // Employer restricted endpoints
                        .requestMatchers("/api/employer/**", "/api/jobs/employer/**").hasAnyRole("EMPLOYER", "ADMIN")
                        // Candidate / Job Seeker restricted endpoints
                        .requestMatchers("/api/job-seeker/**", "/api/resumes/**", "/api/saved-jobs/**").hasAnyRole("EMPLOYEE", "ADMIN")
                        // General authenticated endpoints
                        .anyRequest().authenticated()
                )
                .addFilterBefore(this.correlationIdFilter, UsernamePasswordAuthenticationFilter.class)
                .addFilterBefore(this.jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}
