package com.lakgamana.service;

import com.lakgamana.controller.TicketController.TicketResponse;
import com.lakgamana.entity.Ticket;
import com.lakgamana.repository.TicketRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class TicketService {

    private static final Logger log = LoggerFactory.getLogger(TicketService.class);

    private final TicketRepository ticketRepository;

    public TicketService(TicketRepository ticketRepository) {
        this.ticketRepository = ticketRepository;
    }

    public List<TicketResponse> getUserTickets(Long userId) {
        try {
            List<Ticket> tickets = ticketRepository.findByUserIdOrderByBookingDateDesc(userId);
            
            return tickets.stream()
                    .map(this::mapToTicketResponse)
                    .collect(Collectors.toList());
        } catch (Exception e) {
            log.error("Error fetching user tickets for user ID: {}", userId, e);
            throw new RuntimeException("Failed to fetch user tickets", e);
        }
    }

    public TicketResponse getTicketById(Long ticketId, Long userId) {
        try {
            Ticket ticket = ticketRepository.findByIdAndUserId(ticketId, userId)
                    .orElseThrow(() -> new RuntimeException("Ticket not found or access denied"));
            
            return mapToTicketResponse(ticket);
        } catch (Exception e) {
            log.error("Error fetching ticket ID: {} for user ID: {}", ticketId, userId, e);
            throw new RuntimeException("Failed to fetch ticket details", e);
        }
    }

    public void cancelTicket(Long ticketId, Long userId) {
        try {
            Ticket ticket = ticketRepository.findByIdAndUserId(ticketId, userId)
                    .orElseThrow(() -> new RuntimeException("Ticket not found or access denied"));
            
            if (Ticket.TicketStatus.CANCELLED.equals(ticket.getStatus())) {
                throw new RuntimeException("Ticket is already cancelled");
            }
            
            if (Ticket.TicketStatus.COMPLETED.equals(ticket.getStatus())) {
                throw new RuntimeException("Cannot cancel completed journey");
            }
            
            ticket.setStatus(Ticket.TicketStatus.CANCELLED);
            ticketRepository.save(ticket);
            
            log.info("Ticket {} cancelled by user {}", ticketId, userId);
        } catch (Exception e) {
            log.error("Error cancelling ticket ID: {} for user ID: {}", ticketId, userId, e);
            throw new RuntimeException("Failed to cancel ticket", e);
        }
    }

    private TicketResponse mapToTicketResponse(Ticket ticket) {
        TicketResponse response = new TicketResponse();
        response.setId(ticket.getId());
        response.setTicketNumber(ticket.getTicketNumber());
        response.setTrainName(ticket.getTrain() != null ? ticket.getTrain().getName() : "N/A");
        response.setTrainNumber(ticket.getTrain() != null ? ticket.getTrain().getTrainId() : "N/A");
        response.setFromStation(ticket.getFromStation() != null ? ticket.getFromStation().getName() : "N/A");
        response.setToStation(ticket.getToStation() != null ? ticket.getToStation().getName() : "N/A");
        response.setDepartureTime(ticket.getDepartureTime() != null ? 
                ticket.getDepartureTime().format(DateTimeFormatter.ofPattern("HH:mm")) : "N/A");
        response.setArrivalTime(ticket.getArrivalTime() != null ? 
                ticket.getArrivalTime().format(DateTimeFormatter.ofPattern("HH:mm")) : "N/A");
        response.setJourneyDate(ticket.getJourneyDate() != null ? 
                ticket.getJourneyDate().format(DateTimeFormatter.ofPattern("yyyy-MM-dd")) : "N/A");
        response.setPassengerName(ticket.getPassengerName());
        response.setPassengerEmail(ticket.getPassengerEmail());
        response.setPassengerPhone(ticket.getPassengerPhone());
        response.setSeatNumber(ticket.getSeatNumber());
        response.setCoachNumber(ticket.getCoachNumber());
        response.setTicketClass(ticket.getTicketClass());
        response.setFare(ticket.getFare());
        response.setStatus(ticket.getStatus() != null ? ticket.getStatus().name() : "N/A");
        response.setBookingDate(ticket.getBookingDate() != null ? 
                ticket.getBookingDate().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")) : "N/A");
        response.setPnrNumber(ticket.getPnrNumber());
        
        return response;
    }
}


