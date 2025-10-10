package com.lakgamana.controller;

import com.lakgamana.dto.response.ApiResponse;
import com.lakgamana.entity.User;
import com.lakgamana.security.UserPrincipal;
import com.lakgamana.service.TicketService;
import com.lakgamana.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/tickets")
@Tag(name = "Tickets", description = "User ticket management APIs")
public class TicketController {

    private static final Logger log = LoggerFactory.getLogger(TicketController.class);
    private final TicketService ticketService;
    private final UserService userService;

    public TicketController(TicketService ticketService, UserService userService) {
        this.ticketService = ticketService;
        this.userService = userService;
    }

    @GetMapping("/my-tickets")
    @Operation(summary = "Get user's tickets", description = "Get all tickets for the authenticated user")
    public ResponseEntity<ApiResponse<List<TicketResponse>>> getMyTickets() {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            UserPrincipal userPrincipal = (UserPrincipal) authentication.getPrincipal();
            User user = userService.findByEmail(userPrincipal.getEmail());

            List<TicketResponse> tickets = ticketService.getUserTickets(user.getId());
            
            return ResponseEntity.ok(ApiResponse.success("Tickets retrieved successfully", tickets));
        } catch (Exception e) {
            log.error("Failed to get user tickets", e);
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error("Failed to get tickets: " + e.getMessage()));
        }
    }

    @GetMapping("/{ticketId}")
    @Operation(summary = "Get ticket details", description = "Get details of a specific ticket")
    public ResponseEntity<ApiResponse<TicketResponse>> getTicketDetails(@PathVariable Long ticketId) {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            UserPrincipal userPrincipal = (UserPrincipal) authentication.getPrincipal();
            User user = userService.findByEmail(userPrincipal.getEmail());

            TicketResponse ticket = ticketService.getTicketById(ticketId, user.getId());
            
            return ResponseEntity.ok(ApiResponse.success("Ticket retrieved successfully", ticket));
        } catch (Exception e) {
            log.error("Failed to get ticket details", e);
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error("Failed to get ticket: " + e.getMessage()));
        }
    }

    @PostMapping("/{ticketId}/cancel")
    @Operation(summary = "Cancel ticket", description = "Cancel a specific ticket")
    public ResponseEntity<ApiResponse<String>> cancelTicket(@PathVariable Long ticketId) {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            UserPrincipal userPrincipal = (UserPrincipal) authentication.getPrincipal();
            User user = userService.findByEmail(userPrincipal.getEmail());

            ticketService.cancelTicket(ticketId, user.getId());
            
            return ResponseEntity.ok(ApiResponse.success("Ticket cancelled successfully", "Ticket has been cancelled"));
        } catch (Exception e) {
            log.error("Failed to cancel ticket", e);
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error("Failed to cancel ticket: " + e.getMessage()));
        }
    }

    // DTO Class for Ticket Response
    public static class TicketResponse {
        private Long id;
        private String ticketNumber;
        private String trainName;
        private String trainNumber;
        private String fromStation;
        private String toStation;
        private String departureTime;
        private String arrivalTime;
        private String journeyDate;
        private String passengerName;
        private String passengerEmail;
        private String passengerPhone;
        private String seatNumber;
        private String coachNumber;
        private String ticketClass;
        private Double fare;
        private String status;
        private String bookingDate;
        private String pnrNumber;

        // Getters and setters
        public Long getId() { return id; }
        public void setId(Long id) { this.id = id; }
        public String getTicketNumber() { return ticketNumber; }
        public void setTicketNumber(String ticketNumber) { this.ticketNumber = ticketNumber; }
        public String getTrainName() { return trainName; }
        public void setTrainName(String trainName) { this.trainName = trainName; }
        public String getTrainNumber() { return trainNumber; }
        public void setTrainNumber(String trainNumber) { this.trainNumber = trainNumber; }
        public String getFromStation() { return fromStation; }
        public void setFromStation(String fromStation) { this.fromStation = fromStation; }
        public String getToStation() { return toStation; }
        public void setToStation(String toStation) { this.toStation = toStation; }
        public String getDepartureTime() { return departureTime; }
        public void setDepartureTime(String departureTime) { this.departureTime = departureTime; }
        public String getArrivalTime() { return arrivalTime; }
        public void setArrivalTime(String arrivalTime) { this.arrivalTime = arrivalTime; }
        public String getJourneyDate() { return journeyDate; }
        public void setJourneyDate(String journeyDate) { this.journeyDate = journeyDate; }
        public String getPassengerName() { return passengerName; }
        public void setPassengerName(String passengerName) { this.passengerName = passengerName; }
        public String getPassengerEmail() { return passengerEmail; }
        public void setPassengerEmail(String passengerEmail) { this.passengerEmail = passengerEmail; }
        public String getPassengerPhone() { return passengerPhone; }
        public void setPassengerPhone(String passengerPhone) { this.passengerPhone = passengerPhone; }
        public String getSeatNumber() { return seatNumber; }
        public void setSeatNumber(String seatNumber) { this.seatNumber = seatNumber; }
        public String getCoachNumber() { return coachNumber; }
        public void setCoachNumber(String coachNumber) { this.coachNumber = coachNumber; }
        public String getTicketClass() { return ticketClass; }
        public void setTicketClass(String ticketClass) { this.ticketClass = ticketClass; }
        public Double getFare() { return fare; }
        public void setFare(Double fare) { this.fare = fare; }
        public String getStatus() { return status; }
        public void setStatus(String status) { this.status = status; }
        public String getBookingDate() { return bookingDate; }
        public void setBookingDate(String bookingDate) { this.bookingDate = bookingDate; }
        public String getPnrNumber() { return pnrNumber; }
        public void setPnrNumber(String pnrNumber) { this.pnrNumber = pnrNumber; }
    }
}


