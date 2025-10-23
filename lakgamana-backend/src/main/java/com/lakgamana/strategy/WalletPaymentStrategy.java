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
 * Strategy for processing wallet payments
 */
@Component
public class WalletPaymentStrategy implements PaymentStrategy {

    private final BookingService bookingService;

    public WalletPaymentStrategy(BookingService bookingService) {
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
        payment.setMethod(PaymentMethod.WALLET);
        payment.setWalletProvider(paymentRequest.getWalletProvider());
        payment.setStatus(PaymentStatus.PENDING);
        payment.setTransactionId(generateTransactionId());
        payment.setPaymentDate(LocalDateTime.now());
        payment.setDescription("Wallet Payment via " + paymentRequest.getWalletProvider() + " - " + 
                             booking.getTrain().getName() + " " + booking.getTrain().getRoute());

        // Simulate wallet processing
        simulateWalletProcessing(payment);
        
        return payment;
    }

    @Override
    public PaymentMethod getPaymentMethod() {
        return PaymentMethod.WALLET;
    }

    private String generatePaymentId() {
        return "PAY_" + System.currentTimeMillis() + "_" + UUID.randomUUID().toString().substring(0, 8);
    }

    private String generateTransactionId() {
        return "TXN_" + System.currentTimeMillis() + "_" + UUID.randomUUID().toString().substring(0, 8);
    }

    private void simulateWalletProcessing(Payment payment) {
        // Simulate processing delay
        try {
            Thread.sleep(80); // Wallet payments are usually faster
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        
        // Simulate 99% success rate for wallet payments
        if (Math.random() > 0.01) {
            payment.setStatus(PaymentStatus.COMPLETED);
            payment.setGatewayResponse("Wallet payment processed successfully via " + payment.getWalletProvider());
        } else {
            payment.setStatus(PaymentStatus.PENDING);
            payment.setGatewayResponse("Wallet payment pending verification");
        }
    }
}
