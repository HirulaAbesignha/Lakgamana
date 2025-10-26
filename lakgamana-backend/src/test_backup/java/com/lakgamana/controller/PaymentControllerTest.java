package com.lakgamana.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.lakgamana.dto.request.PaymentRequest;
import com.lakgamana.dto.response.PaymentResponse;
import com.lakgamana.service.PaymentService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Arrays;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(PaymentController.class)
class PaymentControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private PaymentService paymentService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @WithMockUser(roles = "USER")
    void processPayment_ShouldReturnSuccess_WhenValidRequest() throws Exception {
        PaymentRequest request = new PaymentRequest();
        request.setBookingId(1L);
        request.setAmount(new BigDecimal("1200.00"));
        request.setCurrency("LKR");
        request.setMethod("CREDIT_CARD");
        request.setCardNumber("1234567890123456");
        request.setExpiryMonth("12");
        request.setExpiryYear("2025");
        request.setCvv("123");

        PaymentResponse response = PaymentResponse.builder()
                .id(1L)
                .bookingId(1L)
                .userId(1L)
                .userName("John Doe")
                .amount(new BigDecimal("1200.00"))
                .currency("LKR")
                .method("CREDIT_CARD")
                .cardLast4("3456")
                .cardBrand("VISA")
                .status("COMPLETED")
                .transactionId("TXN001234567890")
                .paymentDate(LocalDateTime.now())
                .description("Train booking payment")
                .build();

        when(paymentService.processPayment(any(PaymentRequest.class))).thenReturn(response);

        mockMvc.perform(post("/api/payments/process")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.status").value("COMPLETED"))
                .andExpect(jsonPath("$.data.transactionId").value("TXN001234567890"));
    }

    @Test
    @WithMockUser(roles = "USER")
    void processPayment_ShouldReturnValidationError_WhenInvalidAmount() throws Exception {
        PaymentRequest request = new PaymentRequest();
        request.setBookingId(1L);
        request.setAmount(new BigDecimal("-100.00"));
        request.setCurrency("LKR");
        request.setMethod("CREDIT_CARD");
        request.setCardNumber("1234567890123456");
        request.setExpiryMonth("12");
        request.setExpiryYear("2025");
        request.setCvv("123");

        mockMvc.perform(post("/api/payments/process")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser(roles = "USER")
    void getPaymentById_ShouldReturnSuccess_WhenPaymentExists() throws Exception {
        PaymentResponse response = PaymentResponse.builder()
                .id(1L)
                .bookingId(1L)
                .userId(1L)
                .userName("John Doe")
                .amount(new BigDecimal("1200.00"))
                .currency("LKR")
                .method("CREDIT_CARD")
                .cardLast4("3456")
                .cardBrand("VISA")
                .status("COMPLETED")
                .transactionId("TXN001234567890")
                .paymentDate(LocalDateTime.now())
                .description("Train booking payment")
                .build();

        when(paymentService.getPaymentById(1L)).thenReturn(response);

        mockMvc.perform(get("/api/payments/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.id").value(1))
                .andExpect(jsonPath("$.data.transactionId").value("TXN001234567890"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void getAllPayments_ShouldReturnSuccess_WhenPaymentsExist() throws Exception {
        PaymentResponse response = PaymentResponse.builder()
                .id(1L)
                .bookingId(1L)
                .userId(1L)
                .userName("John Doe")
                .amount(new BigDecimal("1200.00"))
                .currency("LKR")
                .method("CREDIT_CARD")
                .cardLast4("3456")
                .cardBrand("VISA")
                .status("COMPLETED")
                .transactionId("TXN001234567890")
                .paymentDate(LocalDateTime.now())
                .description("Train booking payment")
                .build();

        when(paymentService.getAllPayments()).thenReturn(Arrays.asList(response));

        mockMvc.perform(get("/api/payments"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data[0].status").value("COMPLETED"))
                .andExpect(jsonPath("$.data[0].userName").value("John Doe"));
    }
}
