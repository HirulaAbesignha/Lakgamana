package com.lakgamana.controller;

import com.lakgamana.dto.request.UpdateProfileRequest;
import com.lakgamana.dto.request.ChangePasswordRequest;
import com.lakgamana.dto.response.ApiResponse;
import com.lakgamana.dto.response.AuthResponse;
import com.lakgamana.entity.User;
import com.lakgamana.service.UserService;
import com.lakgamana.security.UserPrincipal;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/profile")
@Slf4j
@Tag(name = "Profile", description = "User profile management APIs")
public class ProfileController {

    private final UserService userService;
    private final PasswordEncoder passwordEncoder;
    private static final org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger(ProfileController.class);

    public ProfileController(UserService userService, PasswordEncoder passwordEncoder) {
        this.userService = userService;
        this.passwordEncoder = passwordEncoder;
    }

    @GetMapping
    @Operation(summary = "Get current user profile", description = "Get profile information of authenticated user")
    public ResponseEntity<ApiResponse<ProfileResponse>> getCurrentProfile() {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            UserPrincipal userPrincipal = (UserPrincipal) authentication.getPrincipal();
            User user = userService.findByEmail(userPrincipal.getEmail());
            
            ProfileResponse profileResponse = mapToProfileResponse(user);
            return ResponseEntity.ok(ApiResponse.success("Profile retrieved successfully", profileResponse));
        } catch (Exception e) {
            log.error("Failed to get current user profile", e);
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error("Failed to get profile: " + e.getMessage()));
        }
    }

    @PutMapping
    @Operation(summary = "Update user profile", description = "Update profile information of authenticated user")
    public ResponseEntity<ApiResponse<ProfileResponse>> updateProfile(@Valid @RequestBody UpdateProfileRequest request) {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            UserPrincipal userPrincipal = (UserPrincipal) authentication.getPrincipal();
            User user = userService.findByEmail(userPrincipal.getEmail());

            // Update user details
            user.setFirstName(request.getFirstName());
            user.setLastName(request.getLastName());
            user.setPhone(request.getPhone());
            
            User updatedUser = userService.updateUser(user.getId(), user);
            ProfileResponse profileResponse = mapToProfileResponse(updatedUser);
            
            return ResponseEntity.ok(ApiResponse.success("Profile updated successfully", profileResponse));
        } catch (Exception e) {
            log.error("Failed to update profile", e);
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error("Failed to update profile: " + e.getMessage()));
        }
    }

    @PutMapping("/change-password")
    @Operation(summary = "Change user password", description = "Change password for authenticated user")
    public ResponseEntity<ApiResponse<String>> changePassword(@Valid @RequestBody ChangePasswordRequest request) {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            UserPrincipal userPrincipal = (UserPrincipal) authentication.getPrincipal();
            User user = userService.findByEmail(userPrincipal.getEmail());

            // Verify current password
            if (!passwordEncoder.matches(request.getCurrentPassword(), user.getPassword())) {
                return ResponseEntity.badRequest()
                        .body(ApiResponse.error("Current password is incorrect"));
            }

            // Validate new password confirmation
            if (!request.getNewPassword().equals(request.getConfirmPassword())) {
                return ResponseEntity.badRequest()
                        .body(ApiResponse.error("New passwords do not match"));
            }

            // Update password
            user.setPassword(passwordEncoder.encode(request.getNewPassword()));
            userService.updateUser(user.getId(), user);
            
            return ResponseEntity.ok(ApiResponse.success("Password changed successfully", "Password updated successfully"));
        } catch (Exception e) {
            log.error("Failed to change password", e);
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error("Failed to change password: " + e.getMessage()));
        }
    }

    @GetMapping("/stats")
    @Operation(summary = "Get user statistics", description = "Get booking statistics for authenticated user")
    public ResponseEntity<ApiResponse<UserStatsResponse>> getUserStats() {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            UserPrincipal userPrincipal = (UserPrincipal) authentication.getPrincipal();
            User user = userService.findByEmail(userPrincipal.getEmail());

            UserStatsResponse stats = new UserStatsResponse();
            stats.setTotalBookings(user.getTotalBookings() != null ? user.getTotalBookings() : 0);
            stats.setMemberSince(user.getCreatedAt());
            stats.setLastLogin(user.getLastLogin());
            
            return ResponseEntity.ok(ApiResponse.success("User statistics retrieved", stats));
        } catch (Exception e) {
            log.error("Failed to get user statistics", e);
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error("Failed to get user statistics: " + e.getMessage()));
        }
    }

    private ProfileResponse mapToProfileResponse(User user) {
        ProfileResponse response = new ProfileResponse();
        response.setId(user.getId());
        response.setUserId(user.getUserId());
        response.setFirstName(user.getFirstName());
        response.setLastName(user.getLastName());
        response.setEmail(user.getEmail());
        response.setPhone(user.getPhone());
        response.setRole(user.getRole());
        response.setStatus(user.getStatus());
        response.setTotalBookings(user.getTotalBookings());
        response.setLastLogin(user.getLastLogin());
        response.setCreatedAt(user.getCreatedAt());
        response.setUpdatedAt(user.getUpdatedAt());
        return response;
    }

    // DTO Classes
    public static class ProfileResponse {
        private Long id;
        private String userId;
        private String firstName;
        private String lastName;
        private String email;
        private String phone;
        private com.lakgamana.entity.enums.UserRole role;
        private com.lakgamana.entity.enums.UserStatus status;
        private Integer totalBookings;
        private java.time.LocalDateTime lastLogin;
        private java.time.LocalDateTime createdAt;
        private java.time.LocalDateTime updatedAt;

        // Getters and setters
        public Long getId() { return id; }
        public void setId(Long id) { this.id = id; }
        public String getUserId() { return userId; }
        public void setUserId(String userId) { this.userId = userId; }
        public String getFirstName() { return firstName; }
        public void setFirstName(String firstName) { this.firstName = firstName; }
        public String getLastName() { return lastName; }
        public void setLastName(String lastName) { this.lastName = lastName; }
        public String getEmail() { return email; }
        public void setEmail(String email) { this.email = email; }
        public String getPhone() { return phone; }
        public void setPhone(String phone) { this.phone = phone; }
        public com.lakgamana.entity.enums.UserRole getRole() { return role; }
        public void setRole(com.lakgamana.entity.enums.UserRole role) { this.role = role; }
        public com.lakgamana.entity.enums.UserStatus getStatus() { return status; }
        public void setStatus(com.lakgamana.entity.enums.UserStatus status) { this.status = status; }
        public Integer getTotalBookings() { return totalBookings; }
        public void setTotalBookings(Integer totalBookings) { this.totalBookings = totalBookings; }
        public java.time.LocalDateTime getLastLogin() { return lastLogin; }
        public void setLastLogin(java.time.LocalDateTime lastLogin) { this.lastLogin = lastLogin; }
        public java.time.LocalDateTime getCreatedAt() { return createdAt; }
        public void setCreatedAt(java.time.LocalDateTime createdAt) { this.createdAt = createdAt; }
        public java.time.LocalDateTime getUpdatedAt() { return updatedAt; }
        public void setUpdatedAt(java.time.LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
    }

    public static class UserStatsResponse {
        private Integer totalBookings;
        private java.time.LocalDateTime memberSince;
        private java.time.LocalDateTime lastLogin;

        // Getters and setters
        public Integer getTotalBookings() { return totalBookings; }
        public void setTotalBookings(Integer totalBookings) { this.totalBookings = totalBookings; }
        public java.time.LocalDateTime getMemberSince() { return memberSince; }
        public void setMemberSince(java.time.LocalDateTime memberSince) { this.memberSince = memberSince; }
        public java.time.LocalDateTime getLastLogin() { return lastLogin; }
        public void setLastLogin(java.time.LocalDateTime lastLogin) { this.lastLogin = lastLogin; }
    }
}
