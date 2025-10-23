package com.lakgamana.strategy;

import com.lakgamana.dto.request.PaymentRequest;
import com.lakgamana.entity.Booking;
import com.lakgamana.entity.Payment;
import com.lakgamana.entity.enums.PaymentMethod;
import com.lakgamana.entity.enums.PaymentStatus;
import com.lakgamana.service.BookingService;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Strategy for processing UPI payments
 */
@Component
public class UPIPaymentStrategy implements PaymentStrategy {

    private final BookingService bookingService;

    public UPIPaymentStrategy(BookingService bookingService) {
        this.bookingService = bookingService;
    }

    @Override
    public Payment processPayment(PaymentRequest paymentRequest) {
        Booking booking = bookingService.findById(paymentRequest.getBookingId());
        
        Payment payment = new Payment();
        payment.setPaymentId(generatePaymentId());
        payment.setBooking(booking);
        payment.setUser(booking.getUser());
        payment.setAmount(paymentRequest.getAmount());
        payment.setCurrency(paymentRequest.getCurrency());
        payment.setMethod(PaymentMethod.UPI);
        payment.setUpiId(paymentRequest.getUpiId());
        payment.setStatus(PaymentStatus.PENDING);
        payment.setTransactionId(generateTransactionId());
        payment.setPaymentDate(LocalDateTime.now());
        payment.setDescription("UPI Payment - " + booking.getTrain().getName() + " " + booking.getTrain().getRoute());

        // Simulate UPI processing
        simulateUPIProcessing(payment);
        
        return payment;
    }

    @Override
    public PaymentMethod getPaymentMethod() {
        return PaymentMethod.UPI;
    }

    private String generatePaymentId() {
        return "PAY_" + System.currentTimeMillis() + "_" + UUID.randomUUID().toString().substring(0, 8);
    }

    private String generateTransactionId() {
        return "TXN_" + System.currentTimeMillis() + "_" + UUID.randomUUID().toString().substring(0, 8);
    }

    private void simulateUPIProcessing(Payment payment) {
        // Simulate processing delay
        try {
            Thread.sleep(150); // UPI might take slightly longer
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        
        // Simulate 98% success rate for UPI
        if (Math.random() > 0.02) {
            payment.setStatus(PaymentStatus.COMPLETED);
            payment.setGatewayResponse("UPI payment processed successfully via " + payment.getUpiId());
        } else {
            payment.setStatus(PaymentStatus.PENDING);
            payment.setGatewayResponse("UPI payment pending user confirmation");
        }
    }
}
