package com.lakgamana.observer;

import com.lakgamana.entity.Payment;
import com.lakgamana.service.EmailService;
import org.springframework.stereotype.Component;

/**
 * Observer that sends email notifications for payment events
 */
@Component
public class EmailNotificationObserver implements PaymentObserver {

    private final EmailService emailService;

    public EmailNotificationObserver(EmailService emailService) {
        this.emailService = emailService;
    }

    @Override
    public void onPaymentCompleted(Payment payment) {
        try {
            emailService.sendPaymentConfirmationEmail(payment);
        } catch (Exception e) {
            System.err.println("Failed to send payment confirmation email: " + e.getMessage());
        }
    }

    @Override
    public void onPaymentFailed(Payment payment) {
        try {
            emailService.sendPaymentFailureEmail(payment);
        } catch (Exception e) {
            System.err.println("Failed to send payment failure email: " + e.getMessage());
        }
    }

    @Override
    public void onPaymentRefunded(Payment payment) {
        try {
            emailService.sendRefundConfirmationEmail(payment);
        } catch (Exception e) {
            System.err.println("Failed to send refund confirmation email: " + e.getMessage());
        }
    }
}
