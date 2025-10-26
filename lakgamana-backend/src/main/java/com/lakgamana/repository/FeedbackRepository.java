package com.lakgamana.repository;

import com.lakgamana.entity.Feedback;
import com.lakgamana.entity.enums.FeedbackCategory;
import com.lakgamana.entity.enums.FeedbackStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface FeedbackRepository extends JpaRepository<Feedback, Long> {

    Optional<Feedback> findByFeedbackId(String feedbackId);

    List<Feedback> findByUserId(Long userId);

    List<Feedback> findByTrainId(Long trainId);

    List<Feedback> findByStatus(FeedbackStatus status);

    List<Feedback> findByCategory(FeedbackCategory category);

    List<Feedback> findByBookingId(Long bookingId);

    @Query("SELECT f FROM Feedback f WHERE " +
           "(:userName IS NULL OR LOWER(f.user.firstName) LIKE LOWER(CONCAT('%', :userName, '%')) OR " +
           "LOWER(f.user.lastName) LIKE LOWER(CONCAT('%', :userName, '%'))) AND " +
           "(:comment IS NULL OR LOWER(f.comment) LIKE LOWER(CONCAT('%', :comment, '%'))) AND " +
           "(:trainName IS NULL OR LOWER(f.train.name) LIKE LOWER(CONCAT('%', :trainName, '%'))) AND " +
           "(:status IS NULL OR f.status = :status)")
    Page<Feedback> findFeedbackWithFilters(
            @Param("userName") String userName,
            @Param("comment") String comment,
            @Param("trainName") String trainName,
            @Param("status") FeedbackStatus status,
            Pageable pageable
    );

    @Query("SELECT AVG(f.rating) FROM Feedback f WHERE f.status = 'REVIEWED'")
    Double getAverageRating();

    @Query("SELECT AVG(f.rating) FROM Feedback f WHERE f.train.id = :trainId AND f.status = 'REVIEWED'")
    Double getAverageRatingForTrain(@Param("trainId") Long trainId);

    @Query("SELECT COUNT(f) FROM Feedback f WHERE f.status = 'REVIEWED'")
    long countApprovedFeedback();

    @Query("SELECT COUNT(f) FROM Feedback f WHERE f.status = 'PENDING'")
    long countPendingFeedback();

    @Query("SELECT f FROM Feedback f WHERE f.status = 'REVIEWED' ORDER BY f.submittedDate DESC")
    List<Feedback> findRecentApprovedFeedback(Pageable pageable);

    @Query("SELECT f FROM Feedback f WHERE f.train.id = :trainId AND f.status = 'REVIEWED' ORDER BY f.submittedDate DESC")
    List<Feedback> findApprovedFeedbackForTrain(@Param("trainId") Long trainId, Pageable pageable);

    @Query("SELECT f.category, COUNT(f) FROM Feedback f WHERE f.status = 'REVIEWED' GROUP BY f.category")
    List<Object[]> getFeedbackCountByCategory();
}
