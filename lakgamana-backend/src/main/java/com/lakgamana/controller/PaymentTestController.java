package com.lakgamana.controller;

import com.lakgamana.dto.request.PaymentRequest;
import com.lakgamana.dto.response.ApiResponse;
import com.lakgamana.dto.response.PaymentResponse;
import com.lakgamana.entity.Payment;
import com.lakgamana.entity.enums.PaymentMethod;
import com.lakgamana.service.EnhancedPaymentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * Test controller to demonstrate Strategy and Observer patterns
 * This controller is for testing purposes only
 */
@RestController
@RequestMapping("/api/test/payments")
@Tag(name = "Payment Test", description = "Test endpoints for Strategy and Observer patterns")
public class PaymentTestController {

    private final EnhancedPaymentService enhancedPaymentService;

    public PaymentTestController(EnhancedPaymentService enhancedPaymentService) {
        this.enhancedPaymentService = enhancedPaymentService;
    }

    @PostMapping("/test-credit-card")
    @Operation(summary = "Test Credit Card Payment", description = "Test credit card payment processing with Strategy pattern")
    public ResponseEntity<ApiResponse<PaymentResponse>> testCreditCardPayment() {
        try {
            // Create a test payment request for credit card
            PaymentRequest request = new PaymentRequest();
            request.setBookingId(1L); // Assuming booking ID 1 exists
            request.setAmount(1500.0);
            request.setCurrency("LKR");
            request.setMethod(PaymentMethod.CREDIT_CARD);
            request.setCardNumber("4111111111111111"); // Test Visa card number
            request.setExpiryDate("12/25");
            request.setCvv("123");
            request.setCardHolderName("Test User");

            Payment payment = enhancedPaymentService.processPayment(request);
            PaymentResponse response = PaymentResponse.fromEntity(payment);
            
            return ResponseEntity.ok(ApiResponse.success(
                "Credit Card payment processed using Strategy pattern. Check logs for email notification.", 
                response
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error("Test failed: " + e.getMessage()));
        }
    }

    @PostMapping("/test-upi")
    @Operation(summary = "Test UPI Payment", description = "Test UPI payment processing with Strategy pattern")
    public ResponseEntity<ApiResponse<PaymentResponse>> testUPIPayment() {
        try {
            // Create a test payment request for UPI
            PaymentRequest request = new PaymentRequest();
            request.setBookingId(1L); // Assuming booking ID 1 exists
            request.setAmount(1200.0);
            request.setCurrency("LKR");
            request.setMethod(PaymentMethod.UPI);
            request.setUpiId("testuser@paytm");

            Payment payment = enhancedPaymentService.processPayment(request);
            PaymentResponse response = PaymentResponse.fromEntity(payment);
            
            return ResponseEntity.ok(ApiResponse.success(
                "UPI payment processed using Strategy pattern. Check logs for email notification.", 
                response
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error("Test failed: " + e.getMessage()));
        }
    }

    @PostMapping("/test-wallet")
    @Operation(summary = "Test Wallet Payment", description = "Test wallet payment processing with Strategy pattern")
    public ResponseEntity<ApiResponse<PaymentResponse>> testWalletPayment() {
        try {
            // Create a test payment request for wallet
            PaymentRequest request = new PaymentRequest();
            request.setBookingId(1L); // Assuming booking ID 1 exists
            request.setAmount(800.0);
            request.setCurrency("LKR");
            request.setMethod(PaymentMethod.WALLET);
            request.setWalletProvider("PayPal");

            Payment payment = enhancedPaymentService.processPayment(request);
            PaymentResponse response = PaymentResponse.fromEntity(payment);
            
            return ResponseEntity.ok(ApiResponse.success(
                "Wallet payment processed using Strategy pattern. Check logs for email notification.", 
                response
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error("Test failed: " + e.getMessage()));
        }
    }

    @GetMapping("/patterns-info")
    @Operation(summary = "Get Pattern Information", description = "Get information about implemented patterns")
    public ResponseEntity<ApiResponse<Object>> getPatternsInfo() {
        var info = new Object() {
            public final String strategyPattern = "Implemented for different payment methods (Credit Card, UPI, Wallet)";
            public final String observerPattern = "Implemented for email notifications on payment events";
            public final String[] paymentStrategies = {"CreditCardPaymentStrategy", "UPIPaymentStrategy", "WalletPaymentStrategy"};
            public final String[] observers = {"EmailNotificationObserver"};
            public final String[] events = {"Payment Completed", "Payment Failed", "Payment Refunded"};
            public final String emailService = "Spring Boot Mail with Gmail SMTP";
        };
        
        return ResponseEntity.ok(ApiResponse.success("Pattern implementation information", info));
    }
}
