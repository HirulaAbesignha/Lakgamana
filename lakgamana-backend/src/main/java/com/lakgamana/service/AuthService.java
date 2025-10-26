package com.lakgamana.service;

import com.lakgamana.dto.request.LoginRequest;
import com.lakgamana.dto.request.RegisterRequest;
import com.lakgamana.dto.response.AuthResponse;
import com.lakgamana.entity.User;
import com.lakgamana.entity.enums.UserRole;
import com.lakgamana.entity.enums.UserStatus;
import com.lakgamana.security.JwtUtil;
import com.lakgamana.security.UserPrincipal;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Slf4j
@Transactional
public class AuthService {

    private final AuthenticationManager authenticationManager;
    private final UserService userService;
    private final JwtUtil jwtUtil;

    public AuthService(AuthenticationManager authenticationManager, UserService userService, JwtUtil jwtUtil) {
        this.authenticationManager = authenticationManager;
        this.userService = userService;
        this.jwtUtil = jwtUtil;
    }

    public AuthResponse login(LoginRequest loginRequest) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginRequest.getEmail(), loginRequest.getPassword())
        );

        SecurityContextHolder.getContext().setAuthentication(authentication);
        
        String jwt = jwtUtil.generateJwtToken(authentication);
        String refreshToken = jwtUtil.generateRefreshToken(authentication.getName());

        UserPrincipal userPrincipal = (UserPrincipal) authentication.getPrincipal();
        User user = userService.findByEmail(userPrincipal.getEmail());

        // Update last login
        userService.updateLastLogin(user.getEmail());

        AuthResponse response = new AuthResponse();
        response.setToken(jwt);
        response.setRefreshToken(refreshToken);
        response.setType("Bearer");
        response.setUser(AuthResponse.UserResponse.fromEntity(user));
        response.setExpiresIn(jwtUtil.getExpirationTime());
        return response;
    }

    public AuthResponse register(RegisterRequest registerRequest) {
        // Validate password confirmation
        if (!registerRequest.getPassword().equals(registerRequest.getConfirmPassword())) {
            throw new RuntimeException("Passwords do not match");
        }

        // Validate terms agreement
        if (!registerRequest.getAgreeToTerms()) {
            throw new RuntimeException("You must agree to the terms and conditions");
        }

        // Create new user
        User user = new User();
        user.setFirstName(registerRequest.getFirstName());
        user.setLastName(registerRequest.getLastName());
        user.setEmail(registerRequest.getEmail());
        user.setPhone(registerRequest.getPhone());
        user.setPassword(registerRequest.getPassword());
        user.setRole(UserRole.USER);
        user.setStatus(UserStatus.ACTIVE);
        user.setTotalBookings(0);

        User savedUser = userService.createUser(user);

        // Generate tokens
        String jwt = jwtUtil.generateTokenFromUsername(savedUser.getEmail());
        String refreshToken = jwtUtil.generateRefreshToken(savedUser.getEmail());

        AuthResponse response = new AuthResponse();
        response.setToken(jwt);
        response.setRefreshToken(refreshToken);
        response.setType("Bearer");
        response.setUser(AuthResponse.UserResponse.fromEntity(savedUser));
        response.setExpiresIn(jwtUtil.getExpirationTime());
        return response;
    }

    public AuthResponse refreshToken(String refreshToken) {
        if (jwtUtil.validateJwtToken(refreshToken)) {
            String email = jwtUtil.getUserNameFromJwtToken(refreshToken);
            User user = userService.findByEmail(email);

            String newJwt = jwtUtil.generateTokenFromUsername(email);
            String newRefreshToken = jwtUtil.generateRefreshToken(email);

            AuthResponse response = new AuthResponse();
            response.setToken(newJwt);
            response.setRefreshToken(newRefreshToken);
            response.setType("Bearer");
            response.setUser(AuthResponse.UserResponse.fromEntity(user));
            response.setExpiresIn(jwtUtil.getExpirationTime());
            return response;
        }
        throw new RuntimeException("Invalid refresh token");
    }

    public AuthResponse.UserResponse getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.getPrincipal() instanceof UserPrincipal) {
            UserPrincipal userPrincipal = (UserPrincipal) authentication.getPrincipal();
            User user = userService.findByEmail(userPrincipal.getEmail());
            return AuthResponse.UserResponse.fromEntity(user);
        }
        throw new RuntimeException("User not authenticated");
    }
}
