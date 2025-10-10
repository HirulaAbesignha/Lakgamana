package com.lakgamana.dto.response;

import com.lakgamana.entity.Payment;
import com.lakgamana.entity.enums.PaymentMethod;
import com.lakgamana.entity.enums.PaymentStatus;
 

import java.time.LocalDateTime;

public class PaymentResponse {

    private Long id;
    private String paymentId;
    private String bookingId;
    private UserResponse user;
    private Double amount;
    private String currency;
    private PaymentMethod method;
    private String cardLast4;
    private String cardBrand;
    private String upiId;
    private String walletProvider;
    private String bankName;
    private PaymentStatus status;
    private String transactionId;
    private LocalDateTime paymentDate;
    private LocalDateTime refundDate;
    private Double refundAmount;
    private String description;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public static class UserResponse {
        private Long id;
        private String userId;
        private String firstName;
        private String lastName;
        private String email;
        public UserResponse() {}
        public Long getId() { return id; }
        public void setId(Long id) { this.id = id; }
        public String getUserId() { return userId; }
        public void setUserId(String userId) { this.userId = userId; }
        public String getFirstName() { return firstName; }
        public void setFirstName(String firstName) { this.firstName = firstName; }
        public String getLastName() { return lastName; }
        public void setLastName(String lastName) { this.lastName = lastName; }
        public String getEmail() { return email; }
        public void setEmail(String email) { this.email = email; }
    }

    public PaymentResponse() {}
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getPaymentId() { return paymentId; }
    public void setPaymentId(String paymentId) { this.paymentId = paymentId; }
    public String getBookingId() { return bookingId; }
    public void setBookingId(String bookingId) { this.bookingId = bookingId; }
    public UserResponse getUser() { return user; }
    public void setUser(UserResponse user) { this.user = user; }
    public Double getAmount() { return amount; }
    public void setAmount(Double amount) { this.amount = amount; }
    public String getCurrency() { return currency; }
    public void setCurrency(String currency) { this.currency = currency; }
    public PaymentMethod getMethod() { return method; }
    public void setMethod(PaymentMethod method) { this.method = method; }
    public String getCardLast4() { return cardLast4; }
    public void setCardLast4(String cardLast4) { this.cardLast4 = cardLast4; }
    public String getCardBrand() { return cardBrand; }
    public void setCardBrand(String cardBrand) { this.cardBrand = cardBrand; }
    public String getUpiId() { return upiId; }
    public void setUpiId(String upiId) { this.upiId = upiId; }
    public String getWalletProvider() { return walletProvider; }
    public void setWalletProvider(String walletProvider) { this.walletProvider = walletProvider; }
    public String getBankName() { return bankName; }
    public void setBankName(String bankName) { this.bankName = bankName; }
    public PaymentStatus getStatus() { return status; }
    public void setStatus(PaymentStatus status) { this.status = status; }
    public String getTransactionId() { return transactionId; }
    public void setTransactionId(String transactionId) { this.transactionId = transactionId; }
    public LocalDateTime getPaymentDate() { return paymentDate; }
    public void setPaymentDate(LocalDateTime paymentDate) { this.paymentDate = paymentDate; }
    public LocalDateTime getRefundDate() { return refundDate; }
    public void setRefundDate(LocalDateTime refundDate) { this.refundDate = refundDate; }
    public Double getRefundAmount() { return refundAmount; }
    public void setRefundAmount(Double refundAmount) { this.refundAmount = refundAmount; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }

    public static PaymentResponse fromEntity(Payment payment) {
        PaymentResponse res = new PaymentResponse();
        res.setId(payment.getId());
        res.setPaymentId(payment.getPaymentId());
        res.setBookingId(payment.getBooking().getBookingId());
        UserResponse ur = new UserResponse();
        ur.setId(payment.getUser().getId());
        ur.setUserId(payment.getUser().getUserId());
        ur.setFirstName(payment.getUser().getFirstName());
        ur.setLastName(payment.getUser().getLastName());
        ur.setEmail(payment.getUser().getEmail());
        res.setUser(ur);
        res.setAmount(payment.getAmount());
        res.setCurrency(payment.getCurrency());
        res.setMethod(payment.getMethod());
        res.setCardLast4(payment.getCardLast4());
        res.setCardBrand(payment.getCardBrand());
        res.setUpiId(payment.getUpiId());
        res.setWalletProvider(payment.getWalletProvider());
        res.setBankName(payment.getBankName());
        res.setStatus(payment.getStatus());
        res.setTransactionId(payment.getTransactionId());
        res.setPaymentDate(payment.getPaymentDate());
        res.setRefundDate(payment.getRefundDate());
        res.setRefundAmount(payment.getRefundAmount());
        res.setDescription(payment.getDescription());
        res.setCreatedAt(payment.getCreatedAt());
        res.setUpdatedAt(payment.getUpdatedAt());
        return res;
    }
}
