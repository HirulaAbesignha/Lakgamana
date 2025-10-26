package com.lakgamana.dto.request;

import com.lakgamana.entity.enums.PaymentMethod;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
 

public class PaymentRequest {

    @NotNull(message = "Booking ID is required")
    private Long bookingId;

    @NotNull(message = "Payment method is required")
    private PaymentMethod method;

    @Positive(message = "Amount must be positive")
    private Double amount;

    @NotBlank(message = "Currency is required")
    @Size(max = 3, message = "Currency must not exceed 3 characters")
    private String currency = "LKR";

    // Credit Card fields
    @Size(max = 19, message = "Card number must not exceed 19 characters")
    private String cardNumber;

    @Size(max = 5, message = "Expiry date must not exceed 5 characters")
    private String expiryDate;

    @Size(max = 4, message = "CVV must not exceed 4 characters")
    private String cvv;

    @Size(max = 100, message = "Card holder name must not exceed 100 characters")
    private String cardHolderName;

    // UPI fields
    @Size(max = 100, message = "UPI ID must not exceed 100 characters")
    private String upiId;

    // Wallet fields
    @Size(max = 50, message = "Wallet provider must not exceed 50 characters")
    private String walletProvider;

    // Bank Transfer fields
    @Size(max = 100, message = "Bank name must not exceed 100 characters")
    private String bankName;

    public PaymentRequest() {}
    public Long getBookingId() { return bookingId; }
    public void setBookingId(Long bookingId) { this.bookingId = bookingId; }
    public PaymentMethod getMethod() { return method; }
    public void setMethod(PaymentMethod method) { this.method = method; }
    public Double getAmount() { return amount; }
    public void setAmount(Double amount) { this.amount = amount; }
    public String getCurrency() { return currency; }
    public void setCurrency(String currency) { this.currency = currency; }
    public String getCardNumber() { return cardNumber; }
    public void setCardNumber(String cardNumber) { this.cardNumber = cardNumber; }
    public String getExpiryDate() { return expiryDate; }
    public void setExpiryDate(String expiryDate) { this.expiryDate = expiryDate; }
    public String getCvv() { return cvv; }
    public void setCvv(String cvv) { this.cvv = cvv; }
    public String getCardHolderName() { return cardHolderName; }
    public void setCardHolderName(String cardHolderName) { this.cardHolderName = cardHolderName; }
    public String getUpiId() { return upiId; }
    public void setUpiId(String upiId) { this.upiId = upiId; }
    public String getWalletProvider() { return walletProvider; }
    public void setWalletProvider(String walletProvider) { this.walletProvider = walletProvider; }
    public String getBankName() { return bankName; }
    public void setBankName(String bankName) { this.bankName = bankName; }
}
