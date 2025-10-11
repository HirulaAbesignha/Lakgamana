package com.lakgamana.repository;

import com.lakgamana.entity.Booking;
import com.lakgamana.entity.enums.BookingStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface BookingRepository extends JpaRepository<Booking, Long> {

    Optional<Booking> findByBookingId(String bookingId);

    List<Booking> findByUserId(Long userId);

    List<Booking> findByUserIdOrderByCreatedAtDesc(Long userId);

    List<Booking> findByTrainId(Long trainId);

    List<Booking> findByStatus(BookingStatus status);

    @Query("SELECT b FROM Booking b WHERE b.user.id = :userId AND " +
           "(:status IS NULL OR b.status = :status) AND " +
           "(:date IS NULL OR b.departureDate = :date)")
    List<Booking> findUserBookingsWithFilters(
            @Param("userId") Long userId,
            @Param("status") BookingStatus status,
            @Param("date") LocalDate date
    );

    @Query("SELECT b FROM Booking b WHERE b.user.id = :userId AND " +
           "b.status != 'CANCELLED' AND " +
           "(:status IS NULL OR b.status = :status) AND " +
           "(:date IS NULL OR b.departureDate = :date)")
    List<Booking> findActiveUserBookingsWithFilters(
            @Param("userId") Long userId,
            @Param("status") BookingStatus status,
            @Param("date") LocalDate date
    );

    @Query("SELECT b FROM Booking b WHERE " +
           "(:userName IS NULL OR LOWER(b.user.firstName) LIKE LOWER(CONCAT('%', :userName, '%')) OR " +
           "LOWER(b.user.lastName) LIKE LOWER(CONCAT('%', :userName, '%'))) AND " +
           "(:trainName IS NULL OR LOWER(b.train.name) LIKE LOWER(CONCAT('%', :trainName, '%'))) AND " +
           "(:bookingId IS NULL OR LOWER(b.bookingId) LIKE LOWER(CONCAT('%', :bookingId, '%'))) AND " +
           "(:status IS NULL OR b.status = :status)")
    Page<Booking> findBookingsWithFilters(
            @Param("userName") String userName,
            @Param("trainName") String trainName,
            @Param("bookingId") String bookingId,
            @Param("status") BookingStatus status,
            Pageable pageable
    );

    @Query("SELECT COUNT(b) FROM Booking b WHERE b.status = 'CONFIRMED'")
    long countConfirmedBookings();

    @Query("SELECT COUNT(b) FROM Booking b WHERE b.status = 'CANCELLED'")
    long countCancelledBookings();

    @Query("SELECT b FROM Booking b WHERE b.status = 'CONFIRMED' ORDER BY b.createdAt DESC")
    List<Booking> findRecentConfirmedBookings(Pageable pageable);

    @Query("SELECT b FROM Booking b WHERE b.train.id = :trainId AND b.departureDate = :date AND b.status = 'CONFIRMED'")
    List<Booking> findConfirmedBookingsForTrainAndDate(@Param("trainId") Long trainId, @Param("date") LocalDate date);
}
