package com.lakgamana.service;

import com.lakgamana.dto.request.FeedbackRequest;
import com.lakgamana.entity.Booking;
import com.lakgamana.entity.Feedback;
import com.lakgamana.entity.Train;
import com.lakgamana.entity.User;
import com.lakgamana.entity.enums.FeedbackCategory;
import com.lakgamana.entity.enums.FeedbackStatus;
import com.lakgamana.repository.FeedbackRepository;
 
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@Transactional
public class FeedbackService {

    private final FeedbackRepository feedbackRepository;
    private final UserService userService;
    private final TrainService trainService;
    private final BookingService bookingService;

    private static final org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger(FeedbackService.class);

    public FeedbackService(FeedbackRepository feedbackRepository, UserService userService,
                           TrainService trainService, BookingService bookingService) {
        this.feedbackRepository = feedbackRepository;
        this.userService = userService;
        this.trainService = trainService;
        this.bookingService = bookingService;
    }

    @Transactional(readOnly = true)
    public Feedback findById(Long id) {
        return feedbackRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Feedback not found with id: " + id));
    }

    @Transactional(readOnly = true)
    public Feedback findByFeedbackId(String feedbackId) {
        return feedbackRepository.findByFeedbackId(feedbackId)
                .orElseThrow(() -> new RuntimeException("Feedback not found with feedbackId: " + feedbackId));
    }

    @Transactional(readOnly = true)
    public List<Feedback> findByUserId(Long userId) {
        return feedbackRepository.findByUserId(userId);
    }

    @Transactional(readOnly = true)
    public List<Feedback> findByTrainId(Long trainId) {
        return feedbackRepository.findByTrainId(trainId);
    }

    @Transactional(readOnly = true)
    public Page<Feedback> findFeedbackWithFilters(String userName, String comment, 
                                                 String trainName, FeedbackStatus status, 
                                                 Pageable pageable) {
        return feedbackRepository.findFeedbackWithFilters(userName, comment, trainName, status, pageable);
    }

    @Transactional(readOnly = true)
    public List<Feedback> findAllFeedback() {
        return feedbackRepository.findAll();
    }

    @Transactional(readOnly = true)
    public List<Feedback> findByStatus(FeedbackStatus status) {
        return feedbackRepository.findByStatus(status);
    }

    @Transactional(readOnly = true)
    public List<Feedback> findByCategory(FeedbackCategory category) {
        return feedbackRepository.findByCategory(category);
    }

    public Feedback createFeedback(Long userId, FeedbackRequest feedbackRequest) {
        User user = userService.findById(userId);
        
        Booking booking = null;
        Train train = null;
        
        if (feedbackRequest.getBookingId() != null) {
            booking = bookingService.findByBookingId(feedbackRequest.getBookingId());
            train = booking.getTrain();
        } else if (feedbackRequest.getTrainId() != null) {
            train = trainService.findById(feedbackRequest.getTrainId());
        }

        Feedback feedback = new Feedback();
        feedback.setFeedbackId(generateFeedbackId());
        feedback.setUser(user);
        feedback.setBooking(booking);
        feedback.setTrain(train);
        feedback.setRating(feedbackRequest.getRating());
        feedback.setTitle(feedbackRequest.getTitle());
        feedback.setComment(feedbackRequest.getComment());
        feedback.setCategory(feedbackRequest.getCategory());
        feedback.setStatus(FeedbackStatus.PENDING);
        feedback.setSubmittedDate(LocalDateTime.now());

        return feedbackRepository.save(feedback);
    }

    public Feedback approveFeedback(Long feedbackId) {
        Feedback feedback = findById(feedbackId);
        feedback.approve();
        feedback.setUpdatedAt(LocalDateTime.now());
        return feedbackRepository.save(feedback);
    }

    public Feedback addAdminResponse(Long feedbackId, String adminResponse) {
        Feedback feedback = findById(feedbackId);
        feedback.addAdminResponse(adminResponse);
        feedback.setUpdatedAt(LocalDateTime.now());
        return feedbackRepository.save(feedback);
    }

    public void deleteFeedback(Long id) {
        Feedback feedback = findById(id);
        feedbackRepository.delete(feedback);
    }

    @Transactional(readOnly = true)
    public Double getAverageRating() {
        Double average = feedbackRepository.getAverageRating();
        return average != null ? average : 0.0;
    }

    @Transactional(readOnly = true)
    public Double getAverageRatingForTrain(Long trainId) {
        Double average = feedbackRepository.getAverageRatingForTrain(trainId);
        return average != null ? average : 0.0;
    }

    @Transactional(readOnly = true)
    public long countApprovedFeedback() {
        return feedbackRepository.countApprovedFeedback();
    }

    @Transactional(readOnly = true)
    public long countPendingFeedback() {
        return feedbackRepository.countPendingFeedback();
    }

    @Transactional(readOnly = true)
    public List<Feedback> findRecentApprovedFeedback(Pageable pageable) {
        return feedbackRepository.findRecentApprovedFeedback(pageable);
    }

    @Transactional(readOnly = true)
    public List<Feedback> findApprovedFeedbackForTrain(Long trainId, Pageable pageable) {
        return feedbackRepository.findApprovedFeedbackForTrain(trainId, pageable);
    }

    @Transactional(readOnly = true)
    public List<Object[]> getFeedbackCountByCategory() {
        return feedbackRepository.getFeedbackCountByCategory();
    }

    private String generateFeedbackId() {
        String feedbackId;
        do {
            feedbackId = "FB" + String.format("%03d", System.currentTimeMillis() % 1000);
        } while (feedbackRepository.findByFeedbackId(feedbackId).isPresent());
        return feedbackId;
    }
}
