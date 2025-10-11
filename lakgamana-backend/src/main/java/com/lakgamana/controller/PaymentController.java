package com.lakgamana.controller;

import com.lakgamana.dto.request.PaymentRequest;
import com.lakgamana.dto.response.ApiResponse;
import com.lakgamana.dto.response.PaymentResponse;
import com.lakgamana.entity.Payment;
import com.lakgamana.entity.enums.PaymentMethod;
import com.lakgamana.entity.enums.PaymentStatus;
import com.lakgamana.service.AuthService;
import com.lakgamana.service.PaymentService;
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

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/payments")
@Tag(name = "Payments", description = "Payment management APIs")
public class PaymentController {

    private final PaymentService paymentService;
    private final AuthService authService;

    private static final org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger(PaymentController.class);

    public PaymentController(PaymentService paymentService, AuthService authService) {
        this.paymentService = paymentService;
        this.authService = authService;
    }

    @PostMapping("/process")
    @Operation(summary = "Process payment", description = "Process a payment for a booking")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<PaymentResponse>> processPayment(@Valid @RequestBody PaymentRequest paymentRequest) {
        try {
            Payment payment = paymentService.processPayment(paymentRequest);
            PaymentResponse paymentResponse = PaymentResponse.fromEntity(payment);
            return ResponseEntity.ok(ApiResponse.success("Payment processed successfully", paymentResponse));
        } catch (Exception e) {
            log.error("Failed to process payment", e);
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error("Failed to process payment: " + e.getMessage()));
        }
    }

    @PutMapping("/{transactionId}/complete")
    @Operation(summary = "Complete payment", description = "Mark payment as completed")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<PaymentResponse>> completePayment(@PathVariable String transactionId) {
        try {
            Payment payment = paymentService.completePayment(transactionId);
            PaymentResponse paymentResponse = PaymentResponse.fromEntity(payment);
            return ResponseEntity.ok(ApiResponse.success("Payment completed successfully", paymentResponse));
        } catch (Exception e) {
            log.error("Failed to complete payment with transaction id: {}", transactionId, e);
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error("Failed to complete payment: " + e.getMessage()));
        }
    }

    @PostMapping("/{paymentId}/refund")
    @Operation(summary = "Process refund", description = "Process a refund for a payment")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<PaymentResponse>> processRefund(
            @PathVariable Long paymentId,
            @RequestParam Double refundAmount) {
        try {
            Payment payment = paymentService.processRefund(paymentId, refundAmount);
            PaymentResponse paymentResponse = PaymentResponse.fromEntity(payment);
            return ResponseEntity.ok(ApiResponse.success("Refund processed successfully", paymentResponse));
        } catch (Exception e) {
            log.error("Failed to process refund for payment id: {}", paymentId, e);
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error("Failed to process refund: " + e.getMessage()));
        }
    }

    @GetMapping("/user")
    @Operation(summary = "Get user payments", description = "Get payment history for current user")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<List<PaymentResponse>>> getUserPayments() {
        try {
            Long userId = authService.getCurrentUser().getId();
            List<Payment> payments = paymentService.findByUserId(userId);
            List<PaymentResponse> paymentResponses = payments.stream()
                    .map(PaymentResponse::fromEntity)
                    .toList();
            return ResponseEntity.ok(ApiResponse.success("User payments retrieved", paymentResponses));
        } catch (Exception e) {
            log.error("Failed to get user payments", e);
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error("Failed to get user payments: " + e.getMessage()));
        }
    }

    @GetMapping("/{paymentId}")
    @Operation(summary = "Get payment by ID", description = "Get payment details by payment ID")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<PaymentResponse>> getPaymentById(@PathVariable Long paymentId) {
        try {
            Payment payment = paymentService.findById(paymentId);
            PaymentResponse paymentResponse = PaymentResponse.fromEntity(payment);
            return ResponseEntity.ok(ApiResponse.success("Payment retrieved", paymentResponse));
        } catch (Exception e) {
            log.error("Failed to get payment with id: {}", paymentId, e);
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error("Failed to get payment: " + e.getMessage()));
        }
    }

    @GetMapping("/transaction/{transactionId}")
    @Operation(summary = "Get payment by transaction ID", description = "Get payment details by transaction ID")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<PaymentResponse>> getPaymentByTransactionId(@PathVariable String transactionId) {
        try {
            Payment payment = paymentService.findByTransactionId(transactionId);
            PaymentResponse paymentResponse = PaymentResponse.fromEntity(payment);
            return ResponseEntity.ok(ApiResponse.success("Payment retrieved", paymentResponse));
        } catch (Exception e) {
            log.error("Failed to get payment with transaction id: {}", transactionId, e);
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error("Failed to get payment: " + e.getMessage()));
        }
    }

    @GetMapping("/admin")
    @Operation(summary = "Get all payments (Admin)", description = "Get all payments with filtering and pagination")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<Page<PaymentResponse>>> getAllPayments(
            @RequestParam(required = false) String userName,
            @RequestParam(required = false) String transactionId,
            @RequestParam(required = false) String bookingId,
            @RequestParam(required = false) PaymentStatus status,
            @RequestParam(required = false) PaymentMethod method,
            @PageableDefault(size = 20, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable) {
        try {
            Page<Payment> payments = paymentService.findPaymentsWithFilters(userName, transactionId, bookingId, status, method, pageable);
            Page<PaymentResponse> paymentResponses = payments.map(PaymentResponse::fromEntity);
            log.info("Retrieved {} payments for admin", payments.getTotalElements());
            return ResponseEntity.ok(ApiResponse.success("Payments retrieved", paymentResponses));
        } catch (Exception e) {
            log.error("Failed to get all payments", e);
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error("Failed to get payments: " + e.getMessage()));
        }
    }

    @GetMapping("/admin/all")
    @Operation(summary = "Get all payments without pagination (Admin)", description = "Get all payments as a simple list")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<List<PaymentResponse>>> getAllPaymentsSimple() {
        try {
            List<Payment> payments = paymentService.findAllPayments();
            List<PaymentResponse> paymentResponses = payments.stream()
                    .map(PaymentResponse::fromEntity)
                    .toList();
            log.info("Retrieved {} payments for admin (simple list)", payments.size());
            return ResponseEntity.ok(ApiResponse.success("Payments retrieved", paymentResponses));
        } catch (Exception e) {
            log.error("Failed to get all payments", e);
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error("Failed to get payments: " + e.getMessage()));
        }
    }

    @GetMapping("/stats")
    @Operation(summary = "Get payment statistics (Admin)", description = "Get payment statistics")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<PaymentStatsResponse>> getPaymentStats() {
        try {
            Double totalRevenue = paymentService.getTotalRevenue();
            Double totalRefunds = paymentService.getTotalRefunds();
            long completedPayments = paymentService.countCompletedPayments();
            long pendingPayments = paymentService.countPendingPayments();
            List<Payment> recentPayments = paymentService.findRecentCompletedPayments(
                    org.springframework.data.domain.PageRequest.of(0, 5));
            
            PaymentStatsResponse stats = new PaymentStatsResponse();
            stats.setTotalRevenue(totalRevenue);
            stats.setTotalRefunds(totalRefunds);
            stats.setCompletedPayments(completedPayments);
            stats.setPendingPayments(pendingPayments);
            stats.setRecentPayments(recentPayments.stream().map(PaymentResponse::fromEntity).toList());
            
            return ResponseEntity.ok(ApiResponse.success("Payment statistics retrieved", stats));
        } catch (Exception e) {
            log.error("Failed to get payment statistics", e);
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error("Failed to get payment statistics: " + e.getMessage()));
        }
    }

    @GetMapping("/revenue")
    @Operation(summary = "Get revenue by date range (Admin)", description = "Get revenue for a specific date range")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<List<PaymentResponse>>> getRevenueByDateRange(
            @RequestParam LocalDateTime startDate,
            @RequestParam LocalDateTime endDate) {
        try {
            List<Payment> payments = paymentService.findPaymentsByDateRange(startDate, endDate);
            List<PaymentResponse> paymentResponses = payments.stream()
                    .map(PaymentResponse::fromEntity)
                    .toList();
            return ResponseEntity.ok(ApiResponse.success("Revenue data retrieved", paymentResponses));
        } catch (Exception e) {
            log.error("Failed to get revenue data", e);
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error("Failed to get revenue data: " + e.getMessage()));
        }
    }

    public static class PaymentStatsResponse {
        private Double totalRevenue;
        private Double totalRefunds;
        private long completedPayments;
        private long pendingPayments;
        private List<PaymentResponse> recentPayments;
        public PaymentStatsResponse() {}
        public Double getTotalRevenue() { return totalRevenue; }
        public void setTotalRevenue(Double totalRevenue) { this.totalRevenue = totalRevenue; }
        public Double getTotalRefunds() { return totalRefunds; }
        public void setTotalRefunds(Double totalRefunds) { this.totalRefunds = totalRefunds; }
        public long getCompletedPayments() { return completedPayments; }
        public void setCompletedPayments(long completedPayments) { this.completedPayments = completedPayments; }
        public long getPendingPayments() { return pendingPayments; }
        public void setPendingPayments(long pendingPayments) { this.pendingPayments = pendingPayments; }
        public List<PaymentResponse> getRecentPayments() { return recentPayments; }
        public void setRecentPayments(List<PaymentResponse> recentPayments) { this.recentPayments = recentPayments; }
    }
}
