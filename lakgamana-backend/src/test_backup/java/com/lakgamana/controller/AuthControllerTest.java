package com.lakgamana.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.lakgamana.dto.request.LoginRequest;
import com.lakgamana.dto.request.RegisterRequest;
import com.lakgamana.dto.response.AuthResponse;
import com.lakgamana.dto.response.ApiResponse;
import com.lakgamana.service.AuthService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(AuthController.class)
class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private AuthService authService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void registerUser_ShouldReturnSuccess_WhenValidRequest() throws Exception {
        RegisterRequest request = new RegisterRequest();
        request.setFirstName("John");
        request.setLastName("Doe");
        request.setEmail("john.doe@example.com");
        request.setPassword("password123");
        request.setPhone("+94 77 123 4567");

        AuthResponse response = AuthResponse.builder()
                .token("jwt-token")
                .refreshToken("refresh-token")
                .user(AuthResponse.UserResponse.builder()
                        .id(1L)
                        .firstName("John")
                        .lastName("Doe")
                        .email("john.doe@example.com")
                        .role("USER")
                        .build())
                .build();

        when(authService.register(any(RegisterRequest.class))).thenReturn(response);

        mockMvc.perform(post("/api/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.token").value("jwt-token"))
                .andExpect(jsonPath("$.data.user.email").value("john.doe@example.com"));
    }

    @Test
    void login_ShouldReturnSuccess_WhenValidCredentials() throws Exception {
        LoginRequest request = new LoginRequest();
        request.setEmail("john.doe@example.com");
        request.setPassword("password123");

        AuthResponse response = AuthResponse.builder()
                .token("jwt-token")
                .refreshToken("refresh-token")
                .user(AuthResponse.UserResponse.builder()
                        .id(1L)
                        .firstName("John")
                        .lastName("Doe")
                        .email("john.doe@example.com")
                        .role("USER")
                        .build())
                .build();

        when(authService.login(any(LoginRequest.class))).thenReturn(response);

        mockMvc.perform(post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.token").value("jwt-token"))
                .andExpect(jsonPath("$.data.user.email").value("john.doe@example.com"));
    }

    @Test
    void registerUser_ShouldReturnValidationError_WhenInvalidEmail() throws Exception {
        RegisterRequest request = new RegisterRequest();
        request.setFirstName("John");
        request.setLastName("Doe");
        request.setEmail("invalid-email");
        request.setPassword("password123");
        request.setPhone("+94 77 123 4567");

        mockMvc.perform(post("/api/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void login_ShouldReturnValidationError_WhenEmptyEmail() throws Exception {
        LoginRequest request = new LoginRequest();
        request.setEmail("");
        request.setPassword("password123");

        mockMvc.perform(post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }
}
