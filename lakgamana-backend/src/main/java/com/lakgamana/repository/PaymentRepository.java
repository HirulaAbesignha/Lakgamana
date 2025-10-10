package com.lakgamana.repository;

import com.lakgamana.entity.Payment;
import com.lakgamana.entity.enums.PaymentMethod;
import com.lakgamana.entity.enums.PaymentStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface PaymentRepository extends JpaRepository<Payment, Long> {

    Optional<Payment> findByPaymentId(String paymentId);

    Optional<Payment> findByTransactionId(String transactionId);

    List<Payment> findByUserId(Long userId);

    List<Payment> findByBookingId(Long bookingId);

    List<Payment> findByStatus(PaymentStatus status);

    @Query("SELECT p FROM Payment p WHERE " +
           "(:userName IS NULL OR LOWER(p.user.firstName) LIKE LOWER(CONCAT('%', :userName, '%')) OR " +
           "LOWER(p.user.lastName) LIKE LOWER(CONCAT('%', :userName, '%'))) AND " +
           "(:transactionId IS NULL OR LOWER(p.transactionId) LIKE LOWER(CONCAT('%', :transactionId, '%'))) AND " +
           "(:bookingId IS NULL OR LOWER(p.booking.bookingId) LIKE LOWER(CONCAT('%', :bookingId, '%'))) AND " +
           "(:status IS NULL OR p.status = :status) AND " +
           "(:method IS NULL OR p.method = :method)")
    Page<Payment> findPaymentsWithFilters(
            @Param("userName") String userName,
            @Param("transactionId") String transactionId,
            @Param("bookingId") String bookingId,
            @Param("status") PaymentStatus status,
            @Param("method") PaymentMethod method,
            Pageable pageable
    );

    @Query("SELECT SUM(p.amount) FROM Payment p WHERE p.status = 'COMPLETED'")
    Double getTotalRevenue();

    @Query("SELECT SUM(p.refundAmount) FROM Payment p WHERE p.status = 'REFUNDED' AND p.refundAmount IS NOT NULL")
    Double getTotalRefunds();

    @Query("SELECT COUNT(p) FROM Payment p WHERE p.status = 'COMPLETED'")
    long countCompletedPayments();

    @Query("SELECT COUNT(p) FROM Payment p WHERE p.status = 'PENDING'")
    long countPendingPayments();

    @Query("SELECT p FROM Payment p WHERE p.status = 'COMPLETED' ORDER BY p.paymentDate DESC")
    List<Payment> findRecentCompletedPayments(Pageable pageable);

    @Query("SELECT p FROM Payment p WHERE p.paymentDate BETWEEN :startDate AND :endDate AND p.status = 'COMPLETED'")
    List<Payment> findPaymentsByDateRange(@Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate);
}
