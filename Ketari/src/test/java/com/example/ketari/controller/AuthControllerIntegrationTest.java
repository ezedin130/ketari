package com.example.ketari.controller;

import com.example.ketari.dto.auth.LoginRequest;
import com.example.ketari.dto.auth.RegisterRequest;
import com.example.ketari.enums.UserRole;
import com.example.ketari.repository.EmployerProfileRepository;
import com.example.ketari.repository.JobSeekerProfileRepository;
import com.example.ketari.repository.UserRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.notNullValue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class AuthControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private JobSeekerProfileRepository jobSeekerProfileRepository;

    @Autowired
    private EmployerProfileRepository employerProfileRepository;

    @BeforeEach
    void setUp() {
        this.jobSeekerProfileRepository.deleteAll();
        this.employerProfileRepository.deleteAll();
        this.userRepository.deleteAll();
    }

    @Test
    @DisplayName("POST /api/auth/register should register a user, set correlation ID header, and return tokens")
    void testRegisterUser() throws Exception {
        RegisterRequest request = RegisterRequest.builder()
                .name("Kalkidan Bekele")
                .email("kalkidan@test.com")
                .password("password123")
                .role(UserRole.EMPLOYEE)
                .build();

        this.mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(this.objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(header().exists("X-Correlation-ID"))
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.accessToken", notNullValue()))
                .andExpect(jsonPath("$.data.refreshToken", notNullValue()))
                .andExpect(jsonPath("$.data.user.email").value("kalkidan@test.com"));
    }

    @Test
    @DisplayName("POST /api/auth/login should authenticate and return tokens")
    void testLoginUser() throws Exception {
        RegisterRequest registerReq = RegisterRequest.builder()
                .name("Solomon Desta")
                .email("solomon@test.com")
                .password("password123")
                .role(UserRole.EMPLOYEE)
                .build();

        this.mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(this.objectMapper.writeValueAsString(registerReq)))
                .andExpect(status().isCreated());

        LoginRequest loginReq = LoginRequest.builder()
                .email("solomon@test.com")
                .password("password123")
                .build();

        this.mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(this.objectMapper.writeValueAsString(loginReq)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.accessToken", notNullValue()))
                .andExpect(jsonPath("$.data.user.name").value("Solomon Desta"));
    }

    @Test
    @DisplayName("GET /api/auth/me should return 401 when unauthenticated")
    void testGetMeUnauthorized() throws Exception {
        this.mockMvc.perform(get("/api/auth/me"))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.status").value(401))
                .andExpect(jsonPath("$.error").value("UNAUTHORIZED"));
    }
}
