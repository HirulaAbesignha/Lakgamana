package com.lakgamana.strategy;

import com.lakgamana.dto.request.PaymentRequest;
import com.lakgamana.entity.Payment;

/**
 * Strategy interface for different payment processing methods
 */
public interface PaymentStrategy {
    
    /**
     * Process payment using specific payment method
     * @param paymentRequest The payment request
     * @return Processed payment entity
     */
    Payment processPayment(PaymentRequest paymentRequest);
    
    /**
     * Get the payment method this strategy handles
     * @return Payment method enum
     */
    com.lakgamana.entity.enums.PaymentMethod getPaymentMethod();
}
