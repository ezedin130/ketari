package com.example.ketari.service;

import com.example.ketari.dto.auth.AuthResponse;
import com.example.ketari.dto.auth.LoginRequest;
import com.example.ketari.dto.auth.RegisterRequest;
import com.example.ketari.enums.UserRole;
import com.example.ketari.exception.DuplicateResourceException;
import com.example.ketari.exception.UnauthorizedException;
import com.example.ketari.model.User;
import com.example.ketari.repository.EmployerProfileRepository;
import com.example.ketari.repository.JobSeekerProfileRepository;
import com.example.ketari.repository.UserRepository;
import com.example.ketari.security.JwtService;
import com.example.ketari.service.impl.AuthServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private JobSeekerProfileRepository jobSeekerProfileRepository;

    @Mock
    private EmployerProfileRepository employerProfileRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JwtService jwtService;

    @Mock
    private AuthenticationManager authenticationManager;

    @InjectMocks
    private AuthServiceImpl authService;

    private User employeeUser;
    private User employerUser;

    @BeforeEach
    void setUp() {
        this.employeeUser = User.builder()
                .name("Abebe Kebede")
                .email("abebe@example.com")
                .password("encoded_pass")
                .role(UserRole.EMPLOYEE)
                .isApproved(true)
                .build();
        this.employeeUser.setId(1L);

        this.employerUser = User.builder()
                .name("TechCorp Employer")
                .email("employer@techcorp.com")
                .password("encoded_pass")
                .role(UserRole.EMPLOYER)
                .isApproved(false)
                .build();
        this.employerUser.setId(2L);
    }

    @Test
    @DisplayName("Should successfully register candidate user")
    void shouldRegisterCandidate() {
        RegisterRequest request = RegisterRequest.builder()
                .name("Abebe Kebede")
                .email("abebe@example.com")
                .password("secret123")
                .role(UserRole.EMPLOYEE)
                .build();

        when(this.userRepository.existsByEmail("abebe@example.com")).thenReturn(false);
        when(this.passwordEncoder.encode("secret123")).thenReturn("encoded_pass");
        when(this.userRepository.save(any(User.class))).thenReturn(this.employeeUser);
        when(this.jwtService.generateAccessToken(this.employeeUser)).thenReturn("access_token");
        when(this.jwtService.generateRefreshToken(this.employeeUser)).thenReturn("refresh_token");
        when(this.jwtService.getAccessTokenExpirationMs()).thenReturn(86400000L);

        AuthResponse response = this.authService.register(request);

        assertThat(response).isNotNull();
        assertThat(response.getAccessToken()).isEqualTo("access_token");
        assertThat(response.getRefreshToken()).isEqualTo("refresh_token");
        assertThat(response.getUser().getEmail()).isEqualTo("abebe@example.com");
        verify(this.jobSeekerProfileRepository).save(any());
    }

    @Test
    @DisplayName("Should throw DuplicateResourceException when email already registered")
    void shouldThrowWhenEmailAlreadyRegistered() {
        RegisterRequest request = RegisterRequest.builder()
                .name("Abebe Kebede")
                .email("abebe@example.com")
                .password("secret123")
                .build();

        when(this.userRepository.existsByEmail("abebe@example.com")).thenReturn(true);

        assertThatThrownBy(() -> this.authService.register(request))
                .isInstanceOf(DuplicateResourceException.class)
                .hasMessageContaining("already exists");
    }

    @Test
    @DisplayName("Should successfully login user with correct credentials")
    void shouldLoginSuccessfully() {
        LoginRequest request = LoginRequest.builder()
                .email("abebe@example.com")
                .password("secret123")
                .build();

        when(this.userRepository.findByEmail("abebe@example.com")).thenReturn(Optional.of(this.employeeUser));
        when(this.passwordEncoder.matches("secret123", "encoded_pass")).thenReturn(true);
        when(this.jwtService.generateAccessToken(this.employeeUser)).thenReturn("access_token");
        when(this.jwtService.generateRefreshToken(this.employeeUser)).thenReturn("refresh_token");
        when(this.jwtService.getAccessTokenExpirationMs()).thenReturn(86400000L);

        AuthResponse response = this.authService.login(request);

        assertThat(response).isNotNull();
        assertThat(response.getAccessToken()).isEqualTo("access_token");
        verify(this.authenticationManager).authenticate(any(UsernamePasswordAuthenticationToken.class));
    }

    @Test
    @DisplayName("Should reject unapproved employer from logging in")
    void shouldRejectUnapprovedEmployer() {
        LoginRequest request = LoginRequest.builder()
                .email("employer@techcorp.com")
                .password("secret123")
                .build();

        when(this.userRepository.findByEmail("employer@techcorp.com")).thenReturn(Optional.of(this.employerUser));
        when(this.passwordEncoder.matches("secret123", "encoded_pass")).thenReturn(true);

        assertThatThrownBy(() -> this.authService.login(request))
                .isInstanceOf(UnauthorizedException.class)
                .hasMessageContaining("pending administrator approval");
    }
}
