package com.lakgamana.strategy;

import com.lakgamana.entity.enums.PaymentMethod;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Factory for managing payment strategies
 */
@Component
public class PaymentStrategyFactory {

    private final Map<PaymentMethod, PaymentStrategy> strategies = new HashMap<>();

    @Autowired
    public PaymentStrategyFactory(List<PaymentStrategy> strategyList) {
        for (PaymentStrategy strategy : strategyList) {
            strategies.put(strategy.getPaymentMethod(), strategy);
        }
    }

    /**
     * Get the appropriate strategy for the given payment method
     * @param paymentMethod The payment method
     * @return The corresponding strategy
     * @throws IllegalArgumentException if no strategy is found for the payment method
     */
    public PaymentStrategy getStrategy(PaymentMethod paymentMethod) {
        PaymentStrategy strategy = strategies.get(paymentMethod);
        if (strategy == null) {
            throw new IllegalArgumentException("No strategy found for payment method: " + paymentMethod);
        }
        return strategy;
    }

    /**
     * Check if a strategy exists for the given payment method
     * @param paymentMethod The payment method
     * @return true if strategy exists, false otherwise
     */
    public boolean hasStrategy(PaymentMethod paymentMethod) {
        return strategies.containsKey(paymentMethod);
    }
}
