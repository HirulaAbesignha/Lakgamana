package com.lakgamana.controller;

import com.lakgamana.entity.User;
import com.lakgamana.entity.enums.UserRole;
import com.lakgamana.entity.enums.UserStatus;
import com.lakgamana.repository.UserRepository;
import com.lakgamana.dto.response.ApiResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

@RestController
@RequestMapping("/setup")
@Slf4j
public class AdminSetupController {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public AdminSetupController(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @PostMapping("/create-admin")
    public ApiResponse<String> createAdminUser() {
        try {
            // Check if admin user already exists
            if (userRepository.existsByEmail("admin@lakgamana.com")) {
                return ApiResponse.success("Admin user already exists", "Admin user is already set up");
            }

            // Create admin user
            User adminUser = new User();
            adminUser.setUserId("ADM001");
            adminUser.setFirstName("Admin");
            adminUser.setLastName("User");
            adminUser.setEmail("admin@lakgamana.com");
            adminUser.setPhone("+94 77 123 4567");
            adminUser.setPassword(passwordEncoder.encode("Admin123!"));
            adminUser.setRole(UserRole.ADMIN);
            adminUser.setStatus(UserStatus.ACTIVE);
            adminUser.setTotalBookings(0);
            adminUser.setCreatedAt(LocalDateTime.now());
            adminUser.setUpdatedAt(LocalDateTime.now());

            // Save admin user
            User savedAdmin = userRepository.save(adminUser);
            
            log.info("Admin user created successfully via API");
            
            return ApiResponse.success("Admin user created successfully", 
                "Email: admin@lakgamana.com, Password: Admin123!");
            
        } catch (Exception e) {
            log.error("Failed to create admin user: {}", e.getMessage(), e);
            return ApiResponse.error("Failed to create admin user: " + e.getMessage());
        }
    }

    @GetMapping("/check-admin")
    public ApiResponse<String> checkAdminUser() {
        try {
            boolean adminExists = userRepository.existsByEmail("admin@lakgamana.com");
            
            if (adminExists) {
                return ApiResponse.success("Admin user exists", "Admin user is already set up");
            } else {
                return ApiResponse.success("Admin user not found", "Admin user needs to be created");
            }
            
        } catch (Exception e) {
            log.error("Failed to check admin user: {}", e.getMessage(), e);
            return ApiResponse.error("Failed to check admin user: " + e.getMessage());
        }
    }
}
