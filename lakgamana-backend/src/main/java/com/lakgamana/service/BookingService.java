package com.lakgamana.service;

import com.lakgamana.dto.request.BookingRequest;
import com.lakgamana.dto.request.PassengerRequest;
import com.lakgamana.entity.Booking;
import com.lakgamana.entity.Passenger;
import com.lakgamana.entity.Payment;
import com.lakgamana.entity.Train;
import com.lakgamana.entity.User;
import com.lakgamana.entity.enums.BookingStatus;
import com.lakgamana.repository.BookingRepository;
import com.lakgamana.repository.PaymentRepository;
 
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@Transactional
public class BookingService {

    private final BookingRepository bookingRepository;
    private final PaymentRepository paymentRepository;
    private final UserService userService;
    private final TrainService trainService;

    private static final org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger(BookingService.class);

    public BookingService(BookingRepository bookingRepository, PaymentRepository paymentRepository, UserService userService, TrainService trainService) {
        this.bookingRepository = bookingRepository;
        this.paymentRepository = paymentRepository;
        this.userService = userService;
        this.trainService = trainService;
    }

    @Transactional(readOnly = true)
    public Booking findById(Long id) {
        return bookingRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Booking not found with id: " + id));
    }

    @Transactional(readOnly = true)
    public Booking findByBookingId(String bookingId) {
        return bookingRepository.findByBookingId(bookingId)
                .orElseThrow(() -> new RuntimeException("Booking not found with bookingId: " + bookingId));
    }

    @Transactional(readOnly = true)
    public List<Booking> findByUserId(Long userId) {
        return bookingRepository.findByUserId(userId);
    }

    @Transactional(readOnly = true)
    public List<Booking> findUserBookingsWithFilters(Long userId, BookingStatus status, 
                                                     java.time.LocalDate date) {
        return bookingRepository.findUserBookingsWithFilters(userId, status, date);
    }

    @Transactional(readOnly = true)
    public List<Booking> findActiveUserBookingsWithFilters(Long userId, BookingStatus status, 
                                                           java.time.LocalDate date) {
        return bookingRepository.findActiveUserBookingsWithFilters(userId, status, date);
    }

    @Transactional(readOnly = true)
    public Page<Booking> findBookingsWithFilters(String userName, String trainName, 
                                                String bookingId, BookingStatus status, 
                                                Pageable pageable) {
        return bookingRepository.findBookingsWithFilters(userName, trainName, bookingId, status, pageable);
    }

    @Transactional(readOnly = true)
    public List<Booking> findAllBookings() {
        return bookingRepository.findAll();
    }

    @Transactional(readOnly = true)
    public List<Booking> findByStatus(BookingStatus status) {
        return bookingRepository.findByStatus(status);
    }

    public Booking createBooking(Long userId, BookingRequest bookingRequest) {
        User user = userService.findById(userId);
        Train train = trainService.findById(bookingRequest.getTrainId());

        // Validate seat availability
        int totalPassengers = bookingRequest.getAdultsCount() + bookingRequest.getChildrenCount();
        if (!train.hasAvailableSeats(bookingRequest.getSeatClass(), totalPassengers)) {
            throw new RuntimeException("Not enough seats available for the selected class");
        }

        // Create booking
        Booking booking = new Booking();
        booking.setBookingId(generateBookingId());
        booking.setUser(user);
        booking.setTrain(train);
        booking.setDepartureDate(bookingRequest.getDepartureDate());
        booking.setDepartureTime(train.getDepartureTime());
        booking.setArrivalTime(train.getArrivalTime());
        booking.setSeatClass(bookingRequest.getSeatClass());
        booking.setSeatNumber(generateSeatNumber(bookingRequest.getSeatClass()));
        booking.setTotalAmount(bookingRequest.getTotalAmount());
        booking.setStatus(BookingStatus.PENDING);
        booking.setBookingDate(LocalDateTime.now());

        // Add passengers
        for (PassengerRequest passengerRequest : bookingRequest.getPassengers()) {
            Passenger passenger = new Passenger();
            passenger.setBooking(booking);
            passenger.setName(passengerRequest.getName());
            passenger.setAge(passengerRequest.getAge());
            passenger.setGender(passengerRequest.getGender());
            passenger.setIdType(passengerRequest.getIdType());
            passenger.setIdNumber(passengerRequest.getIdNumber());
            booking.getPassengers().add(passenger);
        }

        Booking savedBooking = bookingRepository.save(booking);

        // Update seat availability
        trainService.updateSeatAvailability(train.getId(), bookingRequest.getSeatClass(), totalPassengers);

        return savedBooking;
    }

    public Booking confirmBooking(Long bookingId) {
        Booking booking = findById(bookingId);
        booking.setStatus(BookingStatus.CONFIRMED);
        booking.setUpdatedAt(LocalDateTime.now());
        
        // Increment user's booking count
        userService.incrementBookingCount(booking.getUser().getId());
        
        return bookingRepository.save(booking);
    }

    public Booking cancelBooking(Long bookingId, String reason) {
        Booking booking = findById(bookingId);
        
        // Debug logging
        log.info("Attempting to cancel booking ID: {}, Status: {}, Departure Date: {}", 
                bookingId, booking.getStatus(), booking.getDepartureDate());
        
        if (!booking.canBeCancelled()) {
            log.error("Booking {} cannot be cancelled. Status: {}, Departure Date: {}, Current Date: {}", 
                    bookingId, booking.getStatus(), booking.getDepartureDate(), java.time.LocalDate.now());
            throw new RuntimeException("Booking cannot be cancelled");
        }

        booking.cancel(reason);
        
        // Update seat availability (release seats)
        Train train = booking.getTrain();
        int totalPassengers = booking.getPassengers().size();
        // Note: This is a simplified approach. In production, you might want to track seat numbers
        
        return bookingRepository.save(booking);
    }

    public Booking cancelBookingByBookingId(String bookingId, String reason) {
        Booking booking = findByBookingId(bookingId);
        
        if (!booking.canBeCancelled()) {
            throw new RuntimeException("Booking cannot be cancelled");
        }

        booking.cancel(reason);
        
        // Update seat availability (release seats)
        Train train = booking.getTrain();
        int totalPassengers = booking.getPassengers().size();
        // Note: This is a simplified approach. In production, you might want to track seat numbers
        
        return bookingRepository.save(booking);
    }

    @Transactional
    public String processRefund(com.lakgamana.dto.request.RefundRequest refundRequest) {
        // Find booking by bookingId
        Booking booking = findByBookingId(refundRequest.getBookingId());
        
        // Validate booking can be refunded (allow both PENDING and CONFIRMED for ongoing tickets)
        if (booking.getStatus() != BookingStatus.CONFIRMED && booking.getStatus() != BookingStatus.PENDING) {
            throw new IllegalArgumentException("Only ongoing bookings can be refunded");
        }
        
        // Check if booking is ongoing (allow refunds for today and future dates)
        if (booking.getDepartureDate().isBefore(java.time.LocalDate.now().minusDays(1))) {
            throw new IllegalArgumentException("Cannot refund bookings that departed more than 1 day ago");
        }
        
        // Find the payment for this booking
        List<Payment> payments = paymentRepository.findByBookingId(booking.getId());
        if (payments.isEmpty()) {
            throw new IllegalArgumentException("No payment found for this booking");
        }
        Payment payment = payments.get(0); // Get the first payment
        
        // Validate payment can be refunded
        if (payment.getStatus() != com.lakgamana.entity.enums.PaymentStatus.COMPLETED) {
            throw new IllegalArgumentException("Only completed payments can be refunded");
        }
        
        // Process refund
        payment.processRefund(payment.getAmount());
        paymentRepository.save(payment);
        
        // Cancel the booking
        booking.setStatus(BookingStatus.CANCELLED);
        booking.setCancellationReason("Refund requested: " + refundRequest.getReason());
        booking.setCancellationDate(java.time.LocalDateTime.now());
        bookingRepository.save(booking);
        
        return "Refund of LKR " + payment.getAmount() + " processed successfully";
    }

    public void deleteBooking(Long id) {
        Booking booking = findById(id);
        bookingRepository.delete(booking);
    }

    @Transactional(readOnly = true)
    public long countConfirmedBookings() {
        return bookingRepository.countConfirmedBookings();
    }

    @Transactional(readOnly = true)
    public long countCancelledBookings() {
        return bookingRepository.countCancelledBookings();
    }

    @Transactional(readOnly = true)
    public List<Booking> findRecentConfirmedBookings(Pageable pageable) {
        return bookingRepository.findRecentConfirmedBookings(pageable);
    }

    @Transactional(readOnly = true)
    public List<Booking> findConfirmedBookingsForTrainAndDate(Long trainId, java.time.LocalDate date) {
        return bookingRepository.findConfirmedBookingsForTrainAndDate(trainId, date);
    }

    private String generateBookingId() {
        String bookingId;
        do {
            bookingId = "B" + String.format("%03d", System.currentTimeMillis() % 1000);
        } while (bookingRepository.findByBookingId(bookingId).isPresent());
        return bookingId;
    }

    private String generateSeatNumber(String seatClass) {
        String prefix = seatClass.substring(0, 1).toUpperCase();
        int seatNumber = (int) (Math.random() * 100) + 1;
        return prefix + seatNumber;
    }
}
