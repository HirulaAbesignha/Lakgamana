package com.lakgamana.controller;

import com.lakgamana.dto.request.PaymentRequest;
import com.lakgamana.dto.response.ApiResponse;
import com.lakgamana.dto.response.PaymentResponse;
import com.lakgamana.entity.Payment;
import com.lakgamana.entity.enums.PaymentStatus;
import com.lakgamana.service.AuthService;
import com.lakgamana.service.EnhancedPaymentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Enhanced Payment Controller using Strategy and Observer patterns
 */
@RestController
@RequestMapping("/api/v2/payments")
@Tag(name = "Enhanced Payments", description = "Enhanced payment management APIs with Strategy and Observer patterns")
public class EnhancedPaymentController {

    private final EnhancedPaymentService enhancedPaymentService;
    private final AuthService authService;

    private static final org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger(EnhancedPaymentController.class);

    public EnhancedPaymentController(EnhancedPaymentService enhancedPaymentService, AuthService authService) {
        this.enhancedPaymentService = enhancedPaymentService;
        this.authService = authService;
    }

    @PostMapping("/process")
    @Operation(summary = "Process payment with Strategy pattern", description = "Process a payment using the appropriate strategy based on payment method")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<PaymentResponse>> processPayment(@Valid @RequestBody PaymentRequest paymentRequest) {
        try {
            log.info("Processing payment request for booking ID: {}", paymentRequest.getBookingId());
            Payment payment = enhancedPaymentService.processPayment(paymentRequest);
            PaymentResponse paymentResponse = PaymentResponse.fromEntity(payment);
            return ResponseEntity.ok(ApiResponse.success("Payment processed successfully using Strategy pattern", paymentResponse));
        } catch (Exception e) {
            log.error("Failed to process payment", e);
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error("Failed to process payment: " + e.getMessage()));
        }
    }

    @PutMapping("/{transactionId}/complete")
    @Operation(summary = "Complete payment with Observer notifications", description = "Mark payment as completed and trigger observer notifications")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<PaymentResponse>> completePayment(@PathVariable String transactionId) {
        try {
            log.info("Completing payment for transaction ID: {}", transactionId);
            Payment payment = enhancedPaymentService.completePayment(transactionId);
            PaymentResponse paymentResponse = PaymentResponse.fromEntity(payment);
            return ResponseEntity.ok(ApiResponse.success("Payment completed successfully with email notification sent", paymentResponse));
        } catch (Exception e) {
            log.error("Failed to complete payment with transaction id: {}", transactionId, e);
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error("Failed to complete payment: " + e.getMessage()));
        }
    }

    @PostMapping("/{paymentId}/refund")
    @Operation(summary = "Process refund with Observer notifications", description = "Process a refund and trigger observer notifications")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<PaymentResponse>> processRefund(
            @PathVariable Long paymentId,
            @RequestParam Double refundAmount) {
        try {
            log.info("Processing refund for payment ID: {}", paymentId);
            Payment payment = enhancedPaymentService.processRefund(paymentId, refundAmount);
            PaymentResponse paymentResponse = PaymentResponse.fromEntity(payment);
            return ResponseEntity.ok(ApiResponse.success("Refund processed successfully with email notification sent", paymentResponse));
        } catch (Exception e) {
            log.error("Failed to process refund for payment id: {}", paymentId, e);
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error("Failed to process refund: " + e.getMessage()));
        }
    }

    @PutMapping("/{transactionId}/fail")
    @Operation(summary = "Mark payment as failed", description = "Mark payment as failed and trigger observer notifications")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<PaymentResponse>> markPaymentAsFailed(
            @PathVariable String transactionId,
            @RequestParam String reason) {
        try {
            log.info("Marking payment as failed for transaction ID: {}", transactionId);
            Payment payment = enhancedPaymentService.markPaymentAsFailed(transactionId, reason);
            PaymentResponse paymentResponse = PaymentResponse.fromEntity(payment);
            return ResponseEntity.ok(ApiResponse.success("Payment marked as failed with notification sent", paymentResponse));
        } catch (Exception e) {
            log.error("Failed to mark payment as failed for transaction id: {}", transactionId, e);
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error("Failed to mark payment as failed: " + e.getMessage()));
        }
    }

    @GetMapping("/user")
    @Operation(summary = "Get user payments", description = "Get all payments for the authenticated user")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<List<PaymentResponse>>> getUserPayments() {
        try {
            Long userId = authService.getCurrentUser().getId();
            List<Payment> payments = enhancedPaymentService.findByUserId(userId);
            List<PaymentResponse> paymentResponses = payments.stream()
                    .map(PaymentResponse::fromEntity)
                    .toList();
            return ResponseEntity.ok(ApiResponse.success("User payments retrieved successfully", paymentResponses));
        } catch (Exception e) {
            log.error("Failed to get user payments", e);
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error("Failed to get user payments: " + e.getMessage()));
        }
    }

    @GetMapping("/booking/{bookingId}")
    @Operation(summary = "Get booking payments", description = "Get all payments for a specific booking")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<List<PaymentResponse>>> getBookingPayments(@PathVariable Long bookingId) {
        try {
            List<Payment> payments = enhancedPaymentService.findByBookingId(bookingId);
            List<PaymentResponse> paymentResponses = payments.stream()
                    .map(PaymentResponse::fromEntity)
                    .toList();
            return ResponseEntity.ok(ApiResponse.success("Booking payments retrieved successfully", paymentResponses));
        } catch (Exception e) {
            log.error("Failed to get booking payments for booking id: {}", bookingId, e);
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error("Failed to get booking payments: " + e.getMessage()));
        }
    }

    @GetMapping("/all")
    @Operation(summary = "Get all payments", description = "Get all payments (Admin only)")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<List<PaymentResponse>>> getAllPayments() {
        try {
            List<Payment> payments = enhancedPaymentService.findAllPayments();
            List<PaymentResponse> paymentResponses = payments.stream()
                    .map(PaymentResponse::fromEntity)
                    .toList();
            return ResponseEntity.ok(ApiResponse.success("All payments retrieved successfully", paymentResponses));
        } catch (Exception e) {
            log.error("Failed to get all payments", e);
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error("Failed to get all payments: " + e.getMessage()));
        }
    }

    @GetMapping("/status/{status}")
    @Operation(summary = "Get payments by status", description = "Get payments filtered by status")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<List<PaymentResponse>>> getPaymentsByStatus(@PathVariable PaymentStatus status) {
        try {
            List<Payment> payments = enhancedPaymentService.findByStatus(status);
            List<PaymentResponse> paymentResponses = payments.stream()
                    .map(PaymentResponse::fromEntity)
                    .toList();
            return ResponseEntity.ok(ApiResponse.success("Payments by status retrieved successfully", paymentResponses));
        } catch (Exception e) {
            log.error("Failed to get payments by status: {}", status, e);
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error("Failed to get payments by status: " + e.getMessage()));
        }
    }

    @GetMapping("/stats")
    @Operation(summary = "Get payment statistics", description = "Get payment statistics and metrics")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<Object>> getPaymentStats() {
        try {
            Double totalRevenue = enhancedPaymentService.getTotalRevenue();
            Double totalRefunds = enhancedPaymentService.getTotalRefunds();
            long completedPayments = enhancedPaymentService.countCompletedPayments();
            long pendingPayments = enhancedPaymentService.countPendingPayments();
            
            var stats = new Object() {
                public final Double totalRevenue = enhancedPaymentService.getTotalRevenue();
                public final Double totalRefunds = enhancedPaymentService.getTotalRefunds();
                public final Long completedPayments = enhancedPaymentService.countCompletedPayments();
                public final Long pendingPayments = enhancedPaymentService.countPendingPayments();
                public final Double netRevenue = totalRevenue - totalRefunds;
            };
            
            return ResponseEntity.ok(ApiResponse.success("Payment statistics retrieved successfully", stats));
        } catch (Exception e) {
            log.error("Failed to get payment statistics", e);
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error("Failed to get payment statistics: " + e.getMessage()));
        }
    }
}
