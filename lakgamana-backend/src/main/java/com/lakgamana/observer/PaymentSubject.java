package com.lakgamana.observer;

import com.lakgamana.entity.Payment;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

/**
 * Subject class for managing payment observers
 */
@Component
public class PaymentSubject {
    
    private final List<PaymentObserver> observers = new ArrayList<>();
    
    /**
     * Add an observer to the list
     * @param observer The observer to add
     */
    public void addObserver(PaymentObserver observer) {
        if (!observers.contains(observer)) {
            observers.add(observer);
        }
    }
    
    /**
     * Remove an observer from the list
     * @param observer The observer to remove
     */
    public void removeObserver(PaymentObserver observer) {
        observers.remove(observer);
    }
    
    /**
     * Notify all observers when payment is completed
     * @param payment The completed payment
     */
    public void notifyPaymentCompleted(Payment payment) {
        for (PaymentObserver observer : observers) {
            try {
                observer.onPaymentCompleted(payment);
            } catch (Exception e) {
                // Log error but don't stop other observers
                System.err.println("Error notifying observer: " + e.getMessage());
            }
        }
    }
    
    /**
     * Notify all observers when payment fails
     * @param payment The failed payment
     */
    public void notifyPaymentFailed(Payment payment) {
        for (PaymentObserver observer : observers) {
            try {
                observer.onPaymentFailed(payment);
            } catch (Exception e) {
                // Log error but don't stop other observers
                System.err.println("Error notifying observer: " + e.getMessage());
            }
        }
    }
    
    /**
     * Notify all observers when payment is refunded
     * @param payment The refunded payment
     */
    public void notifyPaymentRefunded(Payment payment) {
        for (PaymentObserver observer : observers) {
            try {
                observer.onPaymentRefunded(payment);
            } catch (Exception e) {
                // Log error but don't stop other observers
                System.err.println("Error notifying observer: " + e.getMessage());
            }
        }
    }
}
