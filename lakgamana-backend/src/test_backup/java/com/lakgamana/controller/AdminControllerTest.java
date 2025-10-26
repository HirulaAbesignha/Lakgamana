package com.lakgamana.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.lakgamana.dto.response.DashboardResponse;
import com.lakgamana.service.*;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.util.Arrays;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(AdminController.class)
class AdminControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserService userService;
    
    @MockBean
    private TrainService trainService;
    
    @MockBean
    private BookingService bookingService;
    
    @MockBean
    private PaymentService paymentService;
    
    @MockBean
    private FeedbackService feedbackService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @WithMockUser(roles = "ADMIN")
    void getDashboardStats_ShouldReturnSuccess_WhenStatsExist() throws Exception {
        DashboardResponse.DashboardStats stats = DashboardResponse.DashboardStats.builder()
                .totalTrains(6L)
                .totalReservations(5L)
                .totalUsers(4L)
                .totalRevenue(12000.0)
                .confirmedBookings(3L)
                .cancelledBookings(0L)
                .pendingPayments(1L)
                .build();

        DashboardResponse response = DashboardResponse.builder()
                .stats(stats)
                .recentBookings(Arrays.asList())
                .recentFeedback(Arrays.asList())
                .build();

        mockMvc.perform(get("/api/admin/dashboard"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.stats.totalTrains").value(6))
                .andExpect(jsonPath("$.data.stats.totalReservations").value(5))
                .andExpect(jsonPath("$.data.stats.totalUsers").value(4))
                .andExpect(jsonPath("$.data.stats.totalRevenue").value(12000.0));
    }

    @Test
    @WithMockUser(roles = "USER")
    void getDashboardStats_ShouldReturnForbidden_WhenUserIsNotAdmin() throws Exception {
        mockMvc.perform(get("/api/admin/dashboard"))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void getUsers_ShouldReturnSuccess_WhenUsersExist() throws Exception {
        mockMvc.perform(get("/api/admin/users"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void getTrains_ShouldReturnSuccess_WhenTrainsExist() throws Exception {
        mockMvc.perform(get("/api/admin/trains"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void getBookings_ShouldReturnSuccess_WhenBookingsExist() throws Exception {
        mockMvc.perform(get("/api/admin/bookings"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void getFeedback_ShouldReturnSuccess_WhenFeedbackExists() throws Exception {
        mockMvc.perform(get("/api/admin/feedback"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void getPayments_ShouldReturnSuccess_WhenPaymentsExist() throws Exception {
        mockMvc.perform(get("/api/admin/payments"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true));
    }
}
