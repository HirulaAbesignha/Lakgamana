package com.lakgamana.config;

import com.lakgamana.entity.User;
import com.lakgamana.entity.enums.UserRole;
import com.lakgamana.entity.enums.UserStatus;
import com.lakgamana.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
@Slf4j
public class AdminUserInitializer implements CommandLineRunner {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public AdminUserInitializer(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String... args) throws Exception {
        // Check if admin user already exists
        if (userRepository.existsByEmail("admin@lakgamana.com")) {
            log.info("Admin user already exists. Skipping admin user creation.");
            return;
        }

        try {
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
            
            log.info("Admin user created successfully!");
            log.info("Email: admin@lakgamana.com");
            log.info("Password: Admin123!");
            log.info("User ID: {}", savedAdmin.getUserId());
            
        } catch (Exception e) {
            log.error("Failed to create admin user: {}", e.getMessage(), e);
        }
    }
}
