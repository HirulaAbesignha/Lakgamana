package com.lakgamana.repository;

import com.lakgamana.entity.Ticket;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TicketRepository extends JpaRepository<Ticket, Long> {

    List<Ticket> findByUserIdOrderByBookingDateDesc(Long userId);

    Optional<Ticket> findByIdAndUserId(Long ticketId, Long userId);

    List<Ticket> findByUserIdAndStatusOrderByJourneyDateDesc(Long userId, Ticket.TicketStatus status);

    @Query("SELECT t FROM Ticket t WHERE t.user.id = :userId AND t.journeyDate >= CURRENT_DATE ORDER BY t.journeyDate ASC")
    List<Ticket> findUpcomingTicketsByUserId(@Param("userId") Long userId);

    @Query("SELECT t FROM Ticket t WHERE t.user.id = :userId AND t.journeyDate < CURRENT_DATE ORDER BY t.journeyDate DESC")
    List<Ticket> findPastTicketsByUserId(@Param("userId") Long userId);

    Optional<Ticket> findByTicketNumber(String ticketNumber);

    Optional<Ticket> findByPnrNumber(String pnrNumber);

    boolean existsByTicketNumber(String ticketNumber);

    boolean existsByPnrNumber(String pnrNumber);
}



