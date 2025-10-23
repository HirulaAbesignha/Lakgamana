package com.lakgamana.service;

import com.lakgamana.dto.request.PaymentRequest;
import com.lakgamana.entity.Payment;
import com.lakgamana.entity.enums.PaymentStatus;
import com.lakgamana.observer.EmailNotificationObserver;
import com.lakgamana.observer.PaymentObserver;
import com.lakgamana.observer.PaymentSubject;
import com.lakgamana.repository.PaymentRepository;
import com.lakgamana.strategy.PaymentStrategy;
import com.lakgamana.strategy.PaymentStrategyFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Enhanced Payment Service using Strategy and Observer patterns
 */
@Service
@Transactional
public class EnhancedPaymentService {

    private final PaymentRepository paymentRepository;
    private final PaymentStrategyFactory strategyFactory;
    private final PaymentSubject paymentSubject;
    private final EmailNotificationObserver emailObserver;

    private static final org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger(EnhancedPaymentService.class);

    @Autowired
    public EnhancedPaymentService(PaymentRepository paymentRepository,
                                PaymentStrategyFactory strategyFactory,
                                PaymentSubject paymentSubject,
                                EmailNotificationObserver emailObserver) {
        this.paymentRepository = paymentRepository;
        this.strategyFactory = strategyFactory;
        this.paymentSubject = paymentSubject;
        this.emailObserver = emailObserver;
        
        // Register the email observer
        this.paymentSubject.addObserver(emailObserver);
    }

    /**
     * Process payment using the appropriate strategy
     */
    public Payment processPayment(PaymentRequest paymentRequest) {
        try {
            log.info("Processing payment for booking ID: {}", paymentRequest.getBookingId());
            
            // Get the appropriate strategy for the payment method
            PaymentStrategy strategy = strategyFactory.getStrategy(paymentRequest.getMethod());
            
            // Process payment using the strategy
            Payment payment = strategy.processPayment(paymentRequest);
            
            // Save the payment
            Payment savedPayment = paymentRepository.save(payment);
            
            // Notify observers based on payment status
            if (savedPayment.getStatus() == PaymentStatus.COMPLETED) {
                paymentSubject.notifyPaymentCompleted(savedPayment);
                log.info("Payment completed successfully: {}", savedPayment.getPaymentId());
            } else {
                log.info("Payment pending: {}", savedPayment.getPaymentId());
            }
            
            return savedPayment;
            
        } catch (Exception e) {
            log.error("Error processing payment for booking ID: {}", paymentRequest.getBookingId(), e);
            throw new RuntimeException("Failed to process payment: " + e.getMessage());
        }
    }

    /**
     * Complete a pending payment
     */
    public Payment completePayment(String transactionId) {
        try {
            log.info("Completing payment for transaction ID: {}", transactionId);
            
            Payment payment = paymentRepository.findByTransactionId(transactionId)
                    .orElseThrow(() -> new RuntimeException("Payment not found with transactionId: " + transactionId));
            
            payment.markAsCompleted();
            payment.setUpdatedAt(LocalDateTime.now());
            
            Payment savedPayment = paymentRepository.save(payment);
            
            // Notify observers
            paymentSubject.notifyPaymentCompleted(savedPayment);
            
            log.info("Payment completed successfully: {}", savedPayment.getPaymentId());
            return savedPayment;
            
        } catch (Exception e) {
            log.error("Error completing payment for transaction ID: {}", transactionId, e);
            throw new RuntimeException("Failed to complete payment: " + e.getMessage());
        }
    }

    /**
     * Process refund with observer notification
     */
    public Payment processRefund(Long paymentId, Double refundAmount) {
        try {
            log.info("Processing refund for payment ID: {}", paymentId);
            
            Payment payment = paymentRepository.findById(paymentId)
                    .orElseThrow(() -> new RuntimeException("Payment not found with id: " + paymentId));
            
            if (!payment.isCompleted()) {
                throw new RuntimeException("Cannot refund a payment that is not completed");
            }

            payment.processRefund(refundAmount);
            payment.setUpdatedAt(LocalDateTime.now());
            
            Payment savedPayment = paymentRepository.save(payment);
            
            // Notify observers
            paymentSubject.notifyPaymentRefunded(savedPayment);
            
            log.info("Refund processed successfully for payment ID: {}", paymentId);
            return savedPayment;
            
        } catch (Exception e) {
            log.error("Error processing refund for payment ID: {}", paymentId, e);
            throw new RuntimeException("Failed to process refund: " + e.getMessage());
        }
    }

    /**
     * Mark payment as failed and notify observers
     */
    public Payment markPaymentAsFailed(String transactionId, String reason) {
        try {
            log.info("Marking payment as failed for transaction ID: {}", transactionId);
            
            Payment payment = paymentRepository.findByTransactionId(transactionId)
                    .orElseThrow(() -> new RuntimeException("Payment not found with transactionId: " + transactionId));
            
            payment.setStatus(PaymentStatus.PENDING); // Keep as pending for retry
            payment.setGatewayResponse("Payment failed: " + reason);
            payment.setUpdatedAt(LocalDateTime.now());
            
            Payment savedPayment = paymentRepository.save(payment);
            
            // Notify observers
            paymentSubject.notifyPaymentFailed(savedPayment);
            
            log.info("Payment marked as failed: {}", savedPayment.getPaymentId());
            return savedPayment;
            
        } catch (Exception e) {
            log.error("Error marking payment as failed for transaction ID: {}", transactionId, e);
            throw new RuntimeException("Failed to mark payment as failed: " + e.getMessage());
        }
    }

    /**
     * Get payment by ID
     */
    @Transactional(readOnly = true)
    public Payment findById(Long id) {
        return paymentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Payment not found with id: " + id));
    }

    /**
     * Get payment by transaction ID
     */
    @Transactional(readOnly = true)
    public Payment findByTransactionId(String transactionId) {
        return paymentRepository.findByTransactionId(transactionId)
                .orElseThrow(() -> new RuntimeException("Payment not found with transactionId: " + transactionId));
    }

    /**
     * Get all payments for a user
     */
    @Transactional(readOnly = true)
    public List<Payment> findByUserId(Long userId) {
        return paymentRepository.findByUserId(userId);
    }

    /**
     * Get all payments for a booking
     */
    @Transactional(readOnly = true)
    public List<Payment> findByBookingId(Long bookingId) {
        return paymentRepository.findByBookingId(bookingId);
    }

    /**
     * Get all payments
     */
    @Transactional(readOnly = true)
    public List<Payment> findAllPayments() {
        return paymentRepository.findAll();
    }

    /**
     * Get payments by status
     */
    @Transactional(readOnly = true)
    public List<Payment> findByStatus(PaymentStatus status) {
        return paymentRepository.findByStatus(status);
    }

    /**
     * Get total revenue
     */
    @Transactional(readOnly = true)
    public Double getTotalRevenue() {
        Double revenue = paymentRepository.getTotalRevenue();
        return revenue != null ? revenue : 0.0;
    }

    /**
     * Get total refunds
     */
    @Transactional(readOnly = true)
    public Double getTotalRefunds() {
        Double refunds = paymentRepository.getTotalRefunds();
        return refunds != null ? refunds : 0.0;
    }

    /**
     * Count completed payments
     */
    @Transactional(readOnly = true)
    public long countCompletedPayments() {
        return paymentRepository.countCompletedPayments();
    }

    /**
     * Count pending payments
     */
    @Transactional(readOnly = true)
    public long countPendingPayments() {
        return paymentRepository.countPendingPayments();
    }
}
