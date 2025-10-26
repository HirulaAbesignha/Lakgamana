package com.lakgamana.observer;

import com.lakgamana.entity.Payment;

/**
 * Observer interface for payment events
 */
public interface PaymentObserver {
    
    /**
     * Called when a payment is completed successfully
     * @param payment The completed payment
     */
    void onPaymentCompleted(Payment payment);
    
    /**
     * Called when a payment fails
     * @param payment The failed payment
     */
    void onPaymentFailed(Payment payment);
    
    /**
     * Called when a payment is refunded
     * @param payment The refunded payment
     */
    void onPaymentRefunded(Payment payment);
}
