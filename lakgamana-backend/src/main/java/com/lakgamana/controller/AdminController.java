package com.lakgamana.controller;

import com.lakgamana.dto.response.ApiResponse;
import com.lakgamana.dto.response.DashboardResponse;
import com.lakgamana.entity.User;
import com.lakgamana.entity.enums.UserRole;
import com.lakgamana.entity.enums.UserStatus;
import com.lakgamana.service.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
 
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/admin")
@Tag(name = "Admin", description = "Admin management APIs")
public class AdminController {

    private final UserService userService;
    private final TrainService trainService;
    private final BookingService bookingService;
    private final PaymentService paymentService;
    private final FeedbackService feedbackService;

    private static final org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger(AdminController.class);

    public AdminController(UserService userService, TrainService trainService, BookingService bookingService,
                           PaymentService paymentService, FeedbackService feedbackService) {
        this.userService = userService;
        this.trainService = trainService;
        this.bookingService = bookingService;
        this.paymentService = paymentService;
        this.feedbackService = feedbackService;
    }

    @GetMapping("/dashboard")
    @Operation(summary = "Get admin dashboard", description = "Get dashboard statistics and recent activities")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<DashboardResponse>> getDashboard() {
        try {
            // Get statistics
            long totalTrains = trainService.countActiveTrains();
            long totalReservations = bookingService.countConfirmedBookings() + bookingService.countCancelledBookings();
            long totalUsers = userService.countActiveUsers();
            Double totalRevenue = paymentService.getTotalRevenue();
            long confirmedBookings = bookingService.countConfirmedBookings();
            long cancelledBookings = bookingService.countCancelledBookings();
            long pendingPayments = paymentService.countPendingPayments();

            // Get recent activities
            List<com.lakgamana.dto.response.BookingResponse> recentBookings = bookingService
                    .findRecentConfirmedBookings(org.springframework.data.domain.PageRequest.of(0, 5))
                    .stream()
                    .map(com.lakgamana.dto.response.BookingResponse::fromEntity)
                    .toList();

            List<com.lakgamana.dto.response.FeedbackResponse> recentFeedback = feedbackService
                    .findRecentApprovedFeedback(org.springframework.data.domain.PageRequest.of(0, 5))
                    .stream()
                    .map(com.lakgamana.dto.response.FeedbackResponse::fromEntity)
                    .toList();

            DashboardResponse.DashboardStats stats = new DashboardResponse.DashboardStats();
            stats.setTotalTrains(totalTrains);
            stats.setTotalReservations(totalReservations);
            stats.setTotalUsers(totalUsers);
            stats.setTotalRevenue(totalRevenue);
            stats.setConfirmedBookings(confirmedBookings);
            stats.setCancelledBookings(cancelledBookings);
            stats.setPendingPayments(pendingPayments);

            DashboardResponse dashboard = new DashboardResponse();
            dashboard.setStats(stats);
            dashboard.setRecentBookings(recentBookings);
            dashboard.setRecentFeedback(recentFeedback);

            return ResponseEntity.ok(ApiResponse.success("Dashboard data retrieved", dashboard));
        } catch (Exception e) {
            log.error("Failed to get dashboard data", e);
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error("Failed to get dashboard data: " + e.getMessage()));
        }
    }

    @GetMapping("/users")
    @Operation(summary = "Get all users (Admin)", description = "Get all users with filtering and pagination")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<Page<UserResponse>>> getAllUsers(
            @RequestParam(required = false) String name,
            @RequestParam(required = false) String email,
            @RequestParam(required = false) UserStatus status,
            @RequestParam(required = false) UserRole role,
            @PageableDefault(size = 20, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable) {
        try {
            Page<User> users = userService.findUsersWithFilters(name, email, status, role, pageable);
            Page<UserResponse> userResponses = users.map(this::mapToUserResponse);
            return ResponseEntity.ok(ApiResponse.success("Users retrieved", userResponses));
        } catch (Exception e) {
            log.error("Failed to get users", e);
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error("Failed to get users: " + e.getMessage()));
        }
    }

    @GetMapping("/users/{id}")
    @Operation(summary = "Get user by ID (Admin)", description = "Get user details by ID")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<UserResponse>> getUserById(@PathVariable Long id) {
        try {
            User user = userService.findById(id);
            UserResponse userResponse = mapToUserResponse(user);
            return ResponseEntity.ok(ApiResponse.success("User retrieved", userResponse));
        } catch (Exception e) {
            log.error("Failed to get user with id: {}", id, e);
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error("Failed to get user: " + e.getMessage()));
        }
    }

    @PutMapping("/users/{id}/toggle-status")
    @Operation(summary = "Toggle user status (Admin)", description = "Activate or suspend a user")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<UserResponse>> toggleUserStatus(@PathVariable Long id) {
        try {
            userService.toggleUserStatus(id);
            User user = userService.findById(id);
            UserResponse userResponse = mapToUserResponse(user);
            return ResponseEntity.ok(ApiResponse.success("User status updated successfully", userResponse));
        } catch (Exception e) {
            log.error("Failed to toggle user status for id: {}", id, e);
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error("Failed to update user status: " + e.getMessage()));
        }
    }

    @DeleteMapping("/users/{id}")
    @Operation(summary = "Delete user (Admin)", description = "Delete a user")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<Void>> deleteUser(@PathVariable Long id) {
        try {
            userService.deleteUser(id);
            return ResponseEntity.ok(ApiResponse.success("User deleted successfully", null));
        } catch (Exception e) {
            log.error("Failed to delete user with id: {}", id, e);
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error("Failed to delete user: " + e.getMessage()));
        }
    }

    @GetMapping("/users/stats")
    @Operation(summary = "Get user statistics (Admin)", description = "Get user statistics")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<UserStatsResponse>> getUserStats() {
        try {
            long totalUsers = userService.countActiveUsers();
            List<User> topUsers = userService.findTopUsersByBookings(org.springframework.data.domain.PageRequest.of(0, 5));
            
            UserStatsResponse stats = new UserStatsResponse();
            stats.setTotalUsers(totalUsers);
            stats.setTopUsers(topUsers.stream().map(this::mapToUserResponse).toList());
            
            return ResponseEntity.ok(ApiResponse.success("User statistics retrieved", stats));
        } catch (Exception e) {
            log.error("Failed to get user statistics", e);
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error("Failed to get user statistics: " + e.getMessage()));
        }
    }

    private UserResponse mapToUserResponse(User user) {
        UserResponse ur = new UserResponse();
        ur.setId(user.getId());
        ur.setUserId(user.getUserId());
        ur.setFirstName(user.getFirstName());
        ur.setLastName(user.getLastName());
        ur.setEmail(user.getEmail());
        ur.setPhone(user.getPhone());
        ur.setRole(user.getRole());
        ur.setStatus(user.getStatus());
        ur.setTotalBookings(user.getTotalBookings());
        ur.setLastLogin(user.getLastLogin());
        ur.setCreatedAt(user.getCreatedAt());
        ur.setUpdatedAt(user.getUpdatedAt());
        return ur;
    }

    public static class UserResponse {
        private Long id;
        private String userId;
        private String firstName;
        private String lastName;
        private String email;
        private String phone;
        private UserRole role;
        private UserStatus status;
        private Integer totalBookings;
        private java.time.LocalDateTime lastLogin;
        private java.time.LocalDateTime createdAt;
        private java.time.LocalDateTime updatedAt;
        public UserResponse() {}
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
        public UserRole getRole() { return role; }
        public void setRole(UserRole role) { this.role = role; }
        public UserStatus getStatus() { return status; }
        public void setStatus(UserStatus status) { this.status = status; }
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
        private long totalUsers;
        private List<UserResponse> topUsers;
        public UserStatsResponse() {}
        public long getTotalUsers() { return totalUsers; }
        public void setTotalUsers(long totalUsers) { this.totalUsers = totalUsers; }
        public List<UserResponse> getTopUsers() { return topUsers; }
        public void setTopUsers(List<UserResponse> topUsers) { this.topUsers = topUsers; }
    }
}
