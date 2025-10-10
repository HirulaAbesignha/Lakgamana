package com.lakgamana.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.lakgamana.dto.request.FeedbackRequest;
import com.lakgamana.dto.response.FeedbackResponse;
import com.lakgamana.service.FeedbackService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.Arrays;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(FeedbackController.class)
class FeedbackControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private FeedbackService feedbackService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @WithMockUser(roles = "USER")
    void submitFeedback_ShouldReturnSuccess_WhenValidRequest() throws Exception {
        FeedbackRequest request = new FeedbackRequest();
        request.setBookingId(1L);
        request.setTrainId(1L);
        request.setRating(5);
        request.setTitle("Excellent Service!");
        request.setComment("The train was clean, comfortable, and on time. The staff was very helpful.");
        request.setCategory("SERVICE");

        FeedbackResponse response = FeedbackResponse.builder()
                .id(1L)
                .userId(1L)
                .userName("John Doe")
                .bookingId(1L)
                .trainId(1L)
                .trainName("Intercity Express")
                .rating(5)
                .title("Excellent Service!")
                .comment("The train was clean, comfortable, and on time. The staff was very helpful.")
                .category("SERVICE")
                .status("PENDING")
                .submittedDate(LocalDateTime.now())
                .build();

        when(feedbackService.submitFeedback(any(FeedbackRequest.class), any(Long.class))).thenReturn(response);

        mockMvc.perform(post("/api/feedback/submit")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.rating").value(5))
                .andExpect(jsonPath("$.data.title").value("Excellent Service!"))
                .andExpect(jsonPath("$.data.status").value("PENDING"));
    }

    @Test
    @WithMockUser(roles = "USER")
    void submitFeedback_ShouldReturnValidationError_WhenInvalidRating() throws Exception {
        FeedbackRequest request = new FeedbackRequest();
        request.setBookingId(1L);
        request.setTrainId(1L);
        request.setRating(6);
        request.setTitle("Excellent Service!");
        request.setComment("The train was clean, comfortable, and on time.");
        request.setCategory("SERVICE");

        mockMvc.perform(post("/api/feedback/submit")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void getAllFeedback_ShouldReturnSuccess_WhenFeedbackExists() throws Exception {
        FeedbackResponse response = FeedbackResponse.builder()
                .id(1L)
                .userId(1L)
                .userName("John Doe")
                .bookingId(1L)
                .trainId(1L)
                .trainName("Intercity Express")
                .rating(5)
                .title("Excellent Service!")
                .comment("The train was clean, comfortable, and on time.")
                .category("SERVICE")
                .status("APPROVED")
                .submittedDate(LocalDateTime.now())
                .adminResponse("Thank you for your positive feedback!")
                .build();

        when(feedbackService.getAllFeedback()).thenReturn(Arrays.asList(response));

        mockMvc.perform(get("/api/feedback"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data[0].rating").value(5))
                .andExpect(jsonPath("$.data[0].status").value("APPROVED"))
                .andExpect(jsonPath("$.data[0].adminResponse").value("Thank you for your positive feedback!"));
    }

    @Test
    @WithMockUser(roles = "USER")
    void getUserFeedback_ShouldReturnSuccess_WhenUserHasFeedback() throws Exception {
        FeedbackResponse response = FeedbackResponse.builder()
                .id(1L)
                .userId(1L)
                .userName("John Doe")
                .bookingId(1L)
                .trainId(1L)
                .trainName("Intercity Express")
                .rating(5)
                .title("Excellent Service!")
                .comment("The train was clean, comfortable, and on time.")
                .category("SERVICE")
                .status("APPROVED")
                .submittedDate(LocalDateTime.now())
                .adminResponse("Thank you for your positive feedback!")
                .build();

        when(feedbackService.getUserFeedback(1L)).thenReturn(Arrays.asList(response));

        mockMvc.perform(get("/api/feedback/user/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data[0].userId").value(1))
                .andExpect(jsonPath("$.data[0].rating").value(5));
    }
}
