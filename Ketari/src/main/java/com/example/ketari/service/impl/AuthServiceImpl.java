package com.example.ketari.service.impl;

import com.example.ketari.dto.auth.AuthResponse;
import com.example.ketari.dto.auth.LoginRequest;
import com.example.ketari.dto.auth.RegisterRequest;
import com.example.ketari.dto.auth.TokenRefreshRequest;
import com.example.ketari.enums.UserRole;
import com.example.ketari.exception.DuplicateResourceException;
import com.example.ketari.exception.UnauthorizedException;
import com.example.ketari.mapper.UserMapper;
import com.example.ketari.model.EmployerProfile;
import com.example.ketari.model.JobSeekerProfile;
import com.example.ketari.model.User;
import com.example.ketari.repository.EmployerProfileRepository;
import com.example.ketari.repository.JobSeekerProfileRepository;
import com.example.ketari.repository.UserRepository;
import com.example.ketari.security.JwtService;
import com.example.ketari.service.AuthService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Implementation of AuthService supporting registration, authentication,
 * and stateless JWT token refresh.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;
    private final JobSeekerProfileRepository jobSeekerProfileRepository;
    private final EmployerProfileRepository employerProfileRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;

    @Override
    @Transactional
    public AuthResponse register(RegisterRequest request) {
        String cleanEmail = request.getEmail().trim().toLowerCase();

        if (this.userRepository.existsByEmail(cleanEmail)) {
            throw new DuplicateResourceException("User with email '" + cleanEmail + "' already exists");
        }

        UserRole role = request.getRole() != null ? request.getRole() : UserRole.EMPLOYEE;
        // Employers require admin approval by default
        boolean isApproved = role != UserRole.EMPLOYER;

        User user = User.builder()
                .name(request.getName().trim())
                .email(cleanEmail)
                .password(this.passwordEncoder.encode(request.getPassword()))
                .role(role)
                .isApproved(isApproved)
                .build();

        User savedUser = this.userRepository.save(user);

        // Pre-initialize empty profile depending on role
        if (role == UserRole.EMPLOYEE) {
            JobSeekerProfile profile = JobSeekerProfile.builder()
                    .user(savedUser)
                    .firstName(request.getName().trim())
                    .build();
            this.jobSeekerProfileRepository.save(profile);
        } else if (role == UserRole.EMPLOYER) {
            EmployerProfile profile = EmployerProfile.builder()
                    .user(savedUser)
                    .companyName(request.getName().trim())
                    .build();
            this.employerProfileRepository.save(profile);
        }

        String accessToken = this.jwtService.generateAccessToken(savedUser);
        String refreshToken = this.jwtService.generateRefreshToken(savedUser);

        log.info("Registered new user with email: {} and role: {}", savedUser.getEmail(), savedUser.getRole());

        return AuthResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .tokenType("Bearer")
                .expiresIn(this.jwtService.getAccessTokenExpirationMs())
                .user(UserMapper.toResponse(savedUser))
                .build();
    }

    @Override
    @Transactional(readOnly = true)
    public AuthResponse login(LoginRequest request) {
        String cleanEmail = request.getEmail().trim().toLowerCase();

        User user = this.userRepository.findByEmail(cleanEmail)
                .orElseThrow(() -> new UnauthorizedException("Invalid email or password"));

        if (!this.passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new UnauthorizedException("Invalid email or password");
        }

        if (user.getRole() == UserRole.EMPLOYER && Boolean.FALSE.equals(user.getIsApproved())) {
            throw new UnauthorizedException("Your employer account is pending administrator approval");
        }

        this.authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(cleanEmail, request.getPassword())
        );

        String accessToken = this.jwtService.generateAccessToken(user);
        String refreshToken = this.jwtService.generateRefreshToken(user);

        log.info("User logged in successfully: {}", user.getEmail());

        return AuthResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .tokenType("Bearer")
                .expiresIn(this.jwtService.getAccessTokenExpirationMs())
                .user(UserMapper.toResponse(user))
                .build();
    }

    @Override
    @Transactional(readOnly = true)
    public AuthResponse refreshToken(TokenRefreshRequest request) {
        String refreshToken = request.getRefreshToken();

        if (!this.jwtService.isRefreshToken(refreshToken)) {
            throw new UnauthorizedException("Invalid or expired refresh token");
        }

        String userEmail = this.jwtService.extractUsername(refreshToken);
        if (userEmail == null) {
            throw new UnauthorizedException("Invalid refresh token claims");
        }

        User user = this.userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new UnauthorizedException("User not found for provided token"));

        if (!user.isEnabled()) {
            throw new UnauthorizedException("Account is disabled or pending approval");
        }

        String newAccessToken = this.jwtService.generateAccessToken(user);
        // We can either retain the same refresh token or generate a fresh one
        String newRefreshToken = this.jwtService.generateRefreshToken(user);

        log.info("Refreshed access token for user: {}", user.getEmail());

        return AuthResponse.builder()
                .accessToken(newAccessToken)
                .refreshToken(newRefreshToken)
                .tokenType("Bearer")
                .expiresIn(this.jwtService.getAccessTokenExpirationMs())
                .user(UserMapper.toResponse(user))
                .build();
    }

    @Override
    @Transactional(readOnly = true)
    public User getCurrentAuthenticatedUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new UnauthorizedException("No authenticated user found in security context");
        }

        String email;
        if (authentication.getPrincipal() instanceof UserDetails userDetails) {
            email = userDetails.getUsername();
        } else if (authentication.getPrincipal() instanceof String principalStr) {
            email = principalStr;
        } else {
            throw new UnauthorizedException("Unsupported authentication principal type");
        }

        return this.userRepository.findByEmail(email)
                .orElseThrow(() -> new UnauthorizedException("Authenticated user not found in database: " + email));
    }
}
