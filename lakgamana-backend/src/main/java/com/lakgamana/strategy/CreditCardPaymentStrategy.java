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
 * Strategy for processing credit card payments
 */
@Component
public class CreditCardPaymentStrategy implements PaymentStrategy {

    private final BookingService bookingService;

    public CreditCardPaymentStrategy(BookingService bookingService) {
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
        payment.setMethod(PaymentMethod.CREDIT_CARD);
        payment.setCardLast4(extractLast4Digits(paymentRequest.getCardNumber()));
        payment.setCardBrand(detectCardBrand(paymentRequest.getCardNumber()));
        payment.setStatus(PaymentStatus.PENDING);
        payment.setTransactionId(generateTransactionId());
        payment.setPaymentDate(LocalDateTime.now());
        payment.setDescription("Credit Card Payment - " + booking.getTrain().getName() + " " + booking.getTrain().getRoute());

        // Simulate credit card processing
        simulateCreditCardProcessing(payment);
        
        return payment;
    }

    @Override
    public PaymentMethod getPaymentMethod() {
        return PaymentMethod.CREDIT_CARD;
    }

    private String generatePaymentId() {
        return "PAY_" + System.currentTimeMillis() + "_" + UUID.randomUUID().toString().substring(0, 8);
    }

    private String generateTransactionId() {
        return "TXN_" + System.currentTimeMillis() + "_" + UUID.randomUUID().toString().substring(0, 8);
    }

    private String extractLast4Digits(String cardNumber) {
        if (cardNumber == null || cardNumber.length() < 4) {
            return "0000";
        }
        return cardNumber.substring(cardNumber.length() - 4);
    }

    private String detectCardBrand(String cardNumber) {
        if (cardNumber == null || cardNumber.length() < 4) {
            return "UNKNOWN";
        }
        
        String firstDigit = cardNumber.substring(0, 1);
        switch (firstDigit) {
            case "4":
                return "VISA";
            case "5":
                return "MASTERCARD";
            case "3":
                return "AMEX";
            case "6":
                return "DISCOVER";
            default:
                return "UNKNOWN";
        }
    }

    private void simulateCreditCardProcessing(Payment payment) {
        // Simulate processing delay and validation
        try {
            Thread.sleep(100); // Simulate network delay
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        
        // Simulate 95% success rate
        if (Math.random() > 0.05) {
            payment.setStatus(PaymentStatus.COMPLETED);
            payment.setGatewayResponse("Credit card payment processed successfully");
        } else {
            payment.setStatus(PaymentStatus.PENDING);
            payment.setGatewayResponse("Credit card payment pending verification");
        }
    }
}
