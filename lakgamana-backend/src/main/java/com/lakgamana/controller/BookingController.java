package com.lakgamana.controller;

import com.lakgamana.dto.request.BookingRequest;
import com.lakgamana.dto.response.ApiResponse;
import com.lakgamana.dto.response.BookingResponse;
import com.lakgamana.entity.Booking;
import com.lakgamana.entity.enums.BookingStatus;
import com.lakgamana.service.AuthService;
import com.lakgamana.service.BookingService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
 
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/bookings")
@Tag(name = "Bookings", description = "Booking management APIs")
public class BookingController {

    private final BookingService bookingService;
    private final AuthService authService;

    private static final org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger(BookingController.class);

    public BookingController(BookingService bookingService, AuthService authService) {
        this.bookingService = bookingService;
        this.authService = authService;
    }

    @PostMapping
    @Operation(summary = "Create booking", description = "Create a new train booking")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<BookingResponse>> createBooking(@Valid @RequestBody BookingRequest bookingRequest) {
        try {
            Long userId = authService.getCurrentUser().getId();
            Booking booking = bookingService.createBooking(userId, bookingRequest);
            BookingResponse bookingResponse = BookingResponse.fromEntity(booking);
            return ResponseEntity.ok(ApiResponse.success("Booking created successfully", bookingResponse));
        } catch (Exception e) {
            log.error("Failed to create booking", e);
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error("Failed to create booking: " + e.getMessage()));
        }
    }

    @GetMapping("/user")
    @Operation(summary = "Get user bookings", description = "Get active bookings for current user (excludes cancelled)")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<List<BookingResponse>>> getUserBookings(
            @RequestParam(required = false) BookingStatus status,
            @RequestParam(required = false) LocalDate date) {
        try {
            Long userId = authService.getCurrentUser().getId();
            // Use active bookings method to exclude cancelled bookings for users
            List<Booking> bookings = bookingService.findActiveUserBookingsWithFilters(userId, status, date);
            List<BookingResponse> bookingResponses = bookings.stream()
                    .map(BookingResponse::fromEntity)
                    .toList();
            return ResponseEntity.ok(ApiResponse.success("User bookings retrieved", bookingResponses));
        } catch (Exception e) {
            log.error("Failed to get user bookings", e);
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error("Failed to get user bookings: " + e.getMessage()));
        }
    }

    @GetMapping("/{bookingId}")
    @Operation(summary = "Get booking by ID", description = "Get booking details by booking ID")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<BookingResponse>> getBookingById(@PathVariable String bookingId) {
        try {
            Booking booking = bookingService.findByBookingId(bookingId);
            BookingResponse bookingResponse = BookingResponse.fromEntity(booking);
            return ResponseEntity.ok(ApiResponse.success("Booking retrieved", bookingResponse));
        } catch (Exception e) {
            log.error("Failed to get booking with id: {}", bookingId, e);
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error("Failed to get booking: " + e.getMessage()));
        }
    }

    @PutMapping("/{bookingId}/confirm")
    @Operation(summary = "Confirm booking", description = "Confirm a pending booking")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<BookingResponse>> confirmBooking(@PathVariable Long bookingId) {
        try {
            Booking booking = bookingService.confirmBooking(bookingId);
            BookingResponse bookingResponse = BookingResponse.fromEntity(booking);
            return ResponseEntity.ok(ApiResponse.success("Booking confirmed successfully", bookingResponse));
        } catch (Exception e) {
            log.error("Failed to confirm booking with id: {}", bookingId, e);
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error("Failed to confirm booking: " + e.getMessage()));
        }
    }

    @PutMapping("/{bookingId}/cancel")
    @Operation(summary = "Cancel booking", description = "Cancel a booking")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<BookingResponse>> cancelBooking(
            @PathVariable String bookingId,
            @RequestParam String reason) {
        try {
            Booking cancelledBooking = bookingService.cancelBookingByBookingId(bookingId, reason);
            BookingResponse bookingResponse = BookingResponse.fromEntity(cancelledBooking);
            return ResponseEntity.ok(ApiResponse.success("Booking cancelled successfully", bookingResponse));
        } catch (Exception e) {
            log.error("Failed to cancel booking with id: {}", bookingId, e);
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error("Failed to cancel booking: " + e.getMessage()));
        }
    }

    @GetMapping("/admin")
    @Operation(summary = "Get all bookings (Admin)", description = "Get all bookings with filtering and pagination")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<Page<BookingResponse>>> getAllBookings(
            @RequestParam(required = false) String userName,
            @RequestParam(required = false) String trainName,
            @RequestParam(required = false) String bookingId,
            @RequestParam(required = false) BookingStatus status,
            @PageableDefault(size = 20, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable) {
        try {
            Page<Booking> bookings = bookingService.findBookingsWithFilters(userName, trainName, bookingId, status, pageable);
            Page<BookingResponse> bookingResponses = bookings.map(BookingResponse::fromEntity);
            return ResponseEntity.ok(ApiResponse.success("Bookings retrieved", bookingResponses));
        } catch (Exception e) {
            log.error("Failed to get all bookings", e);
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error("Failed to get bookings: " + e.getMessage()));
        }
    }

    @DeleteMapping("/{bookingId}")
    @Operation(summary = "Delete booking (Admin)", description = "Delete a booking")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<Void>> deleteBooking(@PathVariable String bookingId) {
        try {
            bookingService.deleteBooking(bookingService.findByBookingId(bookingId).getId());
            return ResponseEntity.ok(ApiResponse.success("Booking deleted successfully", null));
        } catch (Exception e) {
            log.error("Failed to delete booking with id: {}", bookingId, e);
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error("Failed to delete booking: " + e.getMessage()));
        }
    }

    @GetMapping("/stats")
    @Operation(summary = "Get booking statistics (Admin)", description = "Get booking statistics")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<BookingStatsResponse>> getBookingStats() {
        try {
            long confirmedBookings = bookingService.countConfirmedBookings();
            long cancelledBookings = bookingService.countCancelledBookings();
            List<Booking> recentBookings = bookingService.findRecentConfirmedBookings(
                    org.springframework.data.domain.PageRequest.of(0, 5));
            
            BookingStatsResponse stats = new BookingStatsResponse();
            stats.setTotalBookings(confirmedBookings + cancelledBookings);
            stats.setConfirmedBookings(confirmedBookings);
            stats.setCancelledBookings(cancelledBookings);
            stats.setRecentBookings(recentBookings.stream().map(BookingResponse::fromEntity).toList());
            
            return ResponseEntity.ok(ApiResponse.success("Booking statistics retrieved", stats));
        } catch (Exception e) {
            log.error("Failed to get booking statistics", e);
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error("Failed to get booking statistics: " + e.getMessage()));
        }
    }

    public static class BookingStatsResponse {
        private long totalBookings;
        private long confirmedBookings;
        private long cancelledBookings;
        private List<BookingResponse> recentBookings;
        public BookingStatsResponse() {}
        public long getTotalBookings() { return totalBookings; }
        public void setTotalBookings(long totalBookings) { this.totalBookings = totalBookings; }
        public long getConfirmedBookings() { return confirmedBookings; }
        public void setConfirmedBookings(long confirmedBookings) { this.confirmedBookings = confirmedBookings; }
        public long getCancelledBookings() { return cancelledBookings; }
        public void setCancelledBookings(long cancelledBookings) { this.cancelledBookings = cancelledBookings; }
        public List<BookingResponse> getRecentBookings() { return recentBookings; }
        public void setRecentBookings(List<BookingResponse> recentBookings) { this.recentBookings = recentBookings; }
    }

    @PostMapping("/refund")
    @Operation(summary = "Request refund for booking", description = "Request refund for an ongoing booking")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    public ResponseEntity<ApiResponse<String>> requestRefund(@RequestBody com.lakgamana.dto.request.RefundRequest refundRequest) {
        try {
            String result = bookingService.processRefund(refundRequest);
            return ResponseEntity.ok(ApiResponse.success("Refund processed successfully", result));
        } catch (Exception e) {
            log.error("Failed to process refund", e);
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error("Failed to process refund: " + e.getMessage()));
        }
    }
}
