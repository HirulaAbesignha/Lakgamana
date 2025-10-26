package com.lakgamana.service;

import com.lakgamana.dto.request.PaymentRequest;
import com.lakgamana.entity.Booking;
import com.lakgamana.entity.Payment;
import com.lakgamana.entity.enums.PaymentMethod;
import com.lakgamana.entity.enums.PaymentStatus;
import com.lakgamana.observer.EmailNotificationObserver;
import com.lakgamana.observer.PaymentSubject;
import com.lakgamana.repository.PaymentRepository;
 
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@Transactional
public class PaymentService {

    private final PaymentRepository paymentRepository;
    private final BookingService bookingService;
    private final PaymentSubject paymentSubject;

    private static final org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger(PaymentService.class);

    @Autowired
    public PaymentService(PaymentRepository paymentRepository, 
                         BookingService bookingService,
                         PaymentSubject paymentSubject,
                         EmailNotificationObserver emailObserver) {
        this.paymentRepository = paymentRepository;
        this.bookingService = bookingService;
        this.paymentSubject = paymentSubject;
        
        // Register the email observer
        this.paymentSubject.addObserver(emailObserver);
    }

    @Transactional(readOnly = true)
    public Payment findById(Long id) {
        return paymentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Payment not found with id: " + id));
    }

    @Transactional(readOnly = true)
    public Payment findByPaymentId(String paymentId) {
        return paymentRepository.findByPaymentId(paymentId)
                .orElseThrow(() -> new RuntimeException("Payment not found with paymentId: " + paymentId));
    }

    @Transactional(readOnly = true)
    public Payment findByTransactionId(String transactionId) {
        return paymentRepository.findByTransactionId(transactionId)
                .orElseThrow(() -> new RuntimeException("Payment not found with transactionId: " + transactionId));
    }

    @Transactional(readOnly = true)
    public List<Payment> findByUserId(Long userId) {
        return paymentRepository.findByUserId(userId);
    }

    @Transactional(readOnly = true)
    public List<Payment> findByBookingId(Long bookingId) {
        return paymentRepository.findByBookingId(bookingId);
    }

    @Transactional(readOnly = true)
    public Page<Payment> findPaymentsWithFilters(String userName, String transactionId, 
                                                String bookingId, PaymentStatus status, 
                                                PaymentMethod method, Pageable pageable) {
        return paymentRepository.findPaymentsWithFilters(userName, transactionId, bookingId, status, method, pageable);
    }

    @Transactional(readOnly = true)
    public List<Payment> findAllPayments() {
        return paymentRepository.findAll();
    }

    @Transactional(readOnly = true)
    public List<Payment> findByStatus(PaymentStatus status) {
        return paymentRepository.findByStatus(status);
    }

    public Payment processPayment(PaymentRequest paymentRequest) {
        try {
            log.info("Processing payment for booking ID: {}", paymentRequest.getBookingId());
            
            // Align with PaymentRequest which uses numeric bookingId
            Booking booking = bookingService.findById(paymentRequest.getBookingId());
            
            // Create payment record
            Payment payment = new Payment();
            payment.setPaymentId(generatePaymentId());
            payment.setBooking(booking);
            payment.setUser(booking.getUser());
            payment.setAmount(paymentRequest.getAmount());
            payment.setCurrency(paymentRequest.getCurrency());
            payment.setMethod(paymentRequest.getMethod());
            payment.setCardLast4(extractLast4Digits(paymentRequest.getCardNumber()));
            payment.setCardBrand(detectCardBrand(paymentRequest.getCardNumber()));
            payment.setUpiId(paymentRequest.getUpiId());
            payment.setWalletProvider(paymentRequest.getWalletProvider());
            payment.setBankName(paymentRequest.getBankName());
            payment.setStatus(PaymentStatus.PENDING);
            payment.setTransactionId(generateTransactionId());
            payment.setPaymentDate(LocalDateTime.now());
            payment.setDescription("Train booking - " + booking.getTrain().getName() + " " + booking.getTrain().getRoute());

            // Save the payment
            Payment savedPayment = paymentRepository.save(payment);
            
            // Simulate payment processing and set status
            simulatePaymentProcessing(savedPayment);
            
            // Save again with updated status
            Payment finalPayment = paymentRepository.save(savedPayment);
            
            // Notify observers based on payment status
            if (finalPayment.getStatus() == PaymentStatus.COMPLETED) {
                paymentSubject.notifyPaymentCompleted(finalPayment);
                log.info("Payment completed successfully: {}", finalPayment.getPaymentId());
            } else if (finalPayment.getStatus() == PaymentStatus.PENDING) {
                log.info("Payment pending: {}", finalPayment.getPaymentId());
            }
            
            return finalPayment;
            
        } catch (Exception e) {
            log.error("Error processing payment for booking ID: {}", paymentRequest.getBookingId(), e);
            throw new RuntimeException("Failed to process payment: " + e.getMessage());
        }
    }

    public Payment completePayment(String transactionId) {
        try {
            log.info("Completing payment for transaction ID: {}", transactionId);
            
            Payment payment = findByTransactionId(transactionId);
            payment.markAsCompleted();
            payment.setUpdatedAt(LocalDateTime.now());
            
            // Don't automatically confirm booking - let admin do it manually
            // bookingService.confirmBooking(payment.getBooking().getId());
            
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

    public Payment processRefund(Long paymentId, Double refundAmount) {
        try {
            log.info("Processing refund for payment ID: {}", paymentId);
            
            Payment payment = findById(paymentId);
            
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

    public void deletePayment(Long id) {
        Payment payment = findById(id);
        paymentRepository.delete(payment);
    }

    @Transactional(readOnly = true)
    public Double getTotalRevenue() {
        Double revenue = paymentRepository.getTotalRevenue();
        return revenue != null ? revenue : 0.0;
    }

    @Transactional(readOnly = true)
    public Double getTotalRefunds() {
        Double refunds = paymentRepository.getTotalRefunds();
        return refunds != null ? refunds : 0.0;
    }

    @Transactional(readOnly = true)
    public long countCompletedPayments() {
        return paymentRepository.countCompletedPayments();
    }

    @Transactional(readOnly = true)
    public long countPendingPayments() {
        return paymentRepository.countPendingPayments();
    }

    @Transactional(readOnly = true)
    public List<Payment> findRecentCompletedPayments(Pageable pageable) {
        return paymentRepository.findRecentCompletedPayments(pageable);
    }

    @Transactional(readOnly = true)
    public List<Payment> findPaymentsByDateRange(LocalDateTime startDate, LocalDateTime endDate) {
        return paymentRepository.findPaymentsByDateRange(startDate, endDate);
    }

    private String generatePaymentId() {
        String paymentId;
        do {
            paymentId = "PAY" + String.format("%03d", System.currentTimeMillis() % 1000);
        } while (paymentRepository.findByPaymentId(paymentId).isPresent());
        return paymentId;
    }

    private String generateTransactionId() {
        return "TXN_" + UUID.randomUUID().toString().replace("-", "").substring(0, 12);
    }

    private String extractLast4Digits(String cardNumber) {
        if (cardNumber != null && cardNumber.length() >= 4) {
            return cardNumber.substring(cardNumber.length() - 4);
        }
        return null;
    }

    private String detectCardBrand(String cardNumber) {
        if (cardNumber == null) return null;
        
        if (cardNumber.startsWith("4")) return "Visa";
        if (cardNumber.startsWith("5")) return "Mastercard";
        if (cardNumber.startsWith("3")) return "American Express";
        
        return "Unknown";
    }

    private void simulatePaymentProcessing(Payment payment) {
        // Simulate processing delay and validation
        try {
            Thread.sleep(100); // Simulate network delay
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        
        // Simulate 95% success rate for all payment methods
        if (Math.random() > 0.05) {
            payment.setStatus(PaymentStatus.COMPLETED);
            payment.setGatewayResponse("Payment processed successfully");
        } else {
            payment.setStatus(PaymentStatus.PENDING);
            payment.setGatewayResponse("Payment pending verification");
        }
    }
}
