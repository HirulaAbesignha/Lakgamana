package com.lakgamana.service;

import com.lakgamana.entity.Payment;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import java.text.NumberFormat;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

/**
 * Service for sending email notifications
 */
@Service
public class EmailService {

    @Autowired
    private JavaMailSender mailSender;

    private static final String FROM_EMAIL = "lakgamana.trains@gmail.com";
    private static final String APP_NAME = "Lakgamana Train Reservations";

    /**
     * Send payment confirmation email
     */
    public void sendPaymentConfirmationEmail(Payment payment) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom(FROM_EMAIL);
            message.setTo(payment.getUser().getEmail());
            message.setSubject("Payment Confirmation - " + APP_NAME);
            message.setText(buildPaymentConfirmationMessage(payment));
            
            mailSender.send(message);
            System.out.println("Payment confirmation email sent to: " + payment.getUser().getEmail());
        } catch (Exception e) {
            System.err.println("Error sending payment confirmation email: " + e.getMessage());
            throw e;
        }
    }

    /**
     * Send payment failure email
     */
    public void sendPaymentFailureEmail(Payment payment) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom(FROM_EMAIL);
            message.setTo(payment.getUser().getEmail());
            message.setSubject("Payment Failed - " + APP_NAME);
            message.setText(buildPaymentFailureMessage(payment));
            
            mailSender.send(message);
            System.out.println("Payment failure email sent to: " + payment.getUser().getEmail());
        } catch (Exception e) {
            System.err.println("Error sending payment failure email: " + e.getMessage());
            throw e;
        }
    }

    /**
     * Send refund confirmation email
     */
    public void sendRefundConfirmationEmail(Payment payment) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom(FROM_EMAIL);
            message.setTo(payment.getUser().getEmail());
            message.setSubject("Refund Confirmation - " + APP_NAME);
            message.setText(buildRefundConfirmationMessage(payment));
            
            mailSender.send(message);
            System.out.println("Refund confirmation email sent to: " + payment.getUser().getEmail());
        } catch (Exception e) {
            System.err.println("Error sending refund confirmation email: " + e.getMessage());
            throw e;
        }
    }

    private String buildPaymentConfirmationMessage(Payment payment) {
        NumberFormat currencyFormat = NumberFormat.getCurrencyInstance(new Locale("en", "LK"));
        
        return String.format("""
            Dear %s,
            
            Your payment has been successfully processed!
            
            Payment Details:
            ================
            Payment ID: %s
            Transaction ID: %s
            Amount: %s
            Payment Method: %s
            Payment Date: %s
            
            Booking Details:
            ================
            Train: %s
            Route: %s
            Departure: %s
            Booking ID: %s
            
            Thank you for choosing Lakgamana Train Reservations!
            
            Best regards,
            Lakgamana Team
            
            ---
            This is an automated message. Please do not reply to this email.
            """,
            payment.getUser().getFirstName() + " " + payment.getUser().getLastName(),
            payment.getPaymentId(),
            payment.getTransactionId(),
            currencyFormat.format(payment.getAmount()),
            payment.getMethod().toString().replace("_", " "),
            payment.getPaymentDate().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")),
            payment.getBooking().getTrain().getName(),
            payment.getBooking().getTrain().getRoute(),
            payment.getBooking().getDepartureDate().format(DateTimeFormatter.ofPattern("yyyy-MM-dd")),
            payment.getBooking().getBookingId()
        );
    }

    private String buildPaymentFailureMessage(Payment payment) {
        NumberFormat currencyFormat = NumberFormat.getCurrencyInstance(new Locale("en", "LK"));
        
        return String.format("""
            Dear %s,
            
            Unfortunately, your payment could not be processed.
            
            Payment Details:
            ================
            Payment ID: %s
            Transaction ID: %s
            Amount: %s
            Payment Method: %s
            Attempted Date: %s
            
            Please try again or contact our support team for assistance.
            
            Booking Details:
            ================
            Train: %s
            Route: %s
            Departure: %s
            Booking ID: %s
            
            If you continue to experience issues, please contact us at support@lakgamana.com
            
            Best regards,
            Lakgamana Team
            
            ---
            This is an automated message. Please do not reply to this email.
            """,
            payment.getUser().getFirstName() + " " + payment.getUser().getLastName(),
            payment.getPaymentId(),
            payment.getTransactionId(),
            currencyFormat.format(payment.getAmount()),
            payment.getMethod().toString().replace("_", " "),
            payment.getPaymentDate().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")),
            payment.getBooking().getTrain().getName(),
            payment.getBooking().getTrain().getRoute(),
            payment.getBooking().getDepartureDate().format(DateTimeFormatter.ofPattern("yyyy-MM-dd")),
            payment.getBooking().getBookingId()
        );
    }

    private String buildRefundConfirmationMessage(Payment payment) {
        NumberFormat currencyFormat = NumberFormat.getCurrencyInstance(new Locale("en", "LK"));
        
        return String.format("""
            Dear %s,
            
            Your refund has been processed successfully!
            
            Refund Details:
            ===============
            Original Payment ID: %s
            Refund Amount: %s
            Refund Date: %s
            Refund Method: %s
            
            The refund will be credited to your original payment method within 3-5 business days.
            
            Original Booking Details:
            ========================
            Train: %s
            Route: %s
            Departure: %s
            Booking ID: %s
            
            If you have any questions about this refund, please contact us at support@lakgamana.com
            
            Best regards,
            Lakgamana Team
            
            ---
            This is an automated message. Please do not reply to this email.
            """,
            payment.getUser().getFirstName() + " " + payment.getUser().getLastName(),
            payment.getPaymentId(),
            currencyFormat.format(payment.getRefundAmount()),
            payment.getRefundDate().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")),
            payment.getMethod().toString().replace("_", " "),
            payment.getBooking().getTrain().getName(),
            payment.getBooking().getTrain().getRoute(),
            payment.getBooking().getDepartureDate().format(DateTimeFormatter.ofPattern("yyyy-MM-dd")),
            payment.getBooking().getBookingId()
        );
    }
}
