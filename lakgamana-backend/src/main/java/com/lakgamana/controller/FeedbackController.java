package com.lakgamana.controller;

import com.lakgamana.dto.request.FeedbackRequest;
import com.lakgamana.dto.response.ApiResponse;
import com.lakgamana.dto.response.FeedbackResponse;
import com.lakgamana.entity.Feedback;
import com.lakgamana.entity.enums.FeedbackStatus;
import com.lakgamana.service.AuthService;
import com.lakgamana.service.FeedbackService;
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

import java.util.List;

@RestController
@RequestMapping("/feedback")
@Tag(name = "Feedback", description = "Feedback management APIs")
public class FeedbackController {

    private final FeedbackService feedbackService;
    private final AuthService authService;

    private static final org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger(FeedbackController.class);

    public FeedbackController(FeedbackService feedbackService, AuthService authService) {
        this.feedbackService = feedbackService;
        this.authService = authService;
    }

    @PostMapping
    @Operation(summary = "Submit feedback", description = "Submit feedback for a train or booking")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<FeedbackResponse>> submitFeedback(@Valid @RequestBody FeedbackRequest feedbackRequest) {
        try {
            Long userId = authService.getCurrentUser().getId();
            Feedback feedback = feedbackService.createFeedback(userId, feedbackRequest);
            FeedbackResponse feedbackResponse = FeedbackResponse.fromEntity(feedback);
            return ResponseEntity.ok(ApiResponse.success("Feedback submitted successfully", feedbackResponse));
        } catch (Exception e) {
            log.error("Failed to submit feedback", e);
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error("Failed to submit feedback: " + e.getMessage()));
        }
    }

    @GetMapping("/user")
    @Operation(summary = "Get user feedback", description = "Get feedback submitted by current user")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<List<FeedbackResponse>>> getUserFeedback() {
        try {
            Long userId = authService.getCurrentUser().getId();
            List<Feedback> feedbacks = feedbackService.findByUserId(userId);
            List<FeedbackResponse> feedbackResponses = feedbacks.stream()
                    .map(FeedbackResponse::fromEntity)
                    .toList();
            return ResponseEntity.ok(ApiResponse.success("User feedback retrieved", feedbackResponses));
        } catch (Exception e) {
            log.error("Failed to get user feedback", e);
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error("Failed to get user feedback: " + e.getMessage()));
        }
    }

    @GetMapping("/train/{trainId}")
    @Operation(summary = "Get feedback for train", description = "Get approved feedback for a specific train")
    public ResponseEntity<ApiResponse<List<FeedbackResponse>>> getTrainFeedback(
            @PathVariable Long trainId,
            @PageableDefault(size = 10, sort = "submittedDate", direction = Sort.Direction.DESC) Pageable pageable) {
        try {
            List<Feedback> feedbacks = feedbackService.findApprovedFeedbackForTrain(trainId, pageable);
            List<FeedbackResponse> feedbackResponses = feedbacks.stream()
                    .map(FeedbackResponse::fromEntity)
                    .toList();
            return ResponseEntity.ok(ApiResponse.success("Train feedback retrieved", feedbackResponses));
        } catch (Exception e) {
            log.error("Failed to get train feedback for train id: {}", trainId, e);
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error("Failed to get train feedback: " + e.getMessage()));
        }
    }

    @GetMapping("/{feedbackId}")
    @Operation(summary = "Get feedback by ID", description = "Get feedback details by feedback ID")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<FeedbackResponse>> getFeedbackById(@PathVariable String feedbackId) {
        try {
            Feedback feedback = feedbackService.findByFeedbackId(feedbackId);
            FeedbackResponse feedbackResponse = FeedbackResponse.fromEntity(feedback);
            return ResponseEntity.ok(ApiResponse.success("Feedback retrieved", feedbackResponse));
        } catch (Exception e) {
            log.error("Failed to get feedback with id: {}", feedbackId, e);
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error("Failed to get feedback: " + e.getMessage()));
        }
    }

    @GetMapping("/admin")
    @Operation(summary = "Get all feedback (Admin)", description = "Get all feedback with filtering and pagination")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<Page<FeedbackResponse>>> getAllFeedback(
            @RequestParam(required = false) String userName,
            @RequestParam(required = false) String comment,
            @RequestParam(required = false) String trainName,
            @RequestParam(required = false) FeedbackStatus status,
            @PageableDefault(size = 20, sort = "submittedDate", direction = Sort.Direction.DESC) Pageable pageable) {
        try {
            Page<Feedback> feedbacks = feedbackService.findFeedbackWithFilters(userName, comment, trainName, status, pageable);
            Page<FeedbackResponse> feedbackResponses = feedbacks.map(FeedbackResponse::fromEntity);
            return ResponseEntity.ok(ApiResponse.success("Feedback retrieved", feedbackResponses));
        } catch (Exception e) {
            log.error("Failed to get all feedback", e);
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error("Failed to get feedback: " + e.getMessage()));
        }
    }

    @PutMapping("/{feedbackId}/approve")
    @Operation(summary = "Approve feedback (Admin)", description = "Approve a pending feedback")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<FeedbackResponse>> approveFeedback(@PathVariable String feedbackId) {
        try {
            Feedback feedback = feedbackService.approveFeedback(feedbackService.findByFeedbackId(feedbackId).getId());
            FeedbackResponse feedbackResponse = FeedbackResponse.fromEntity(feedback);
            return ResponseEntity.ok(ApiResponse.success("Feedback approved successfully", feedbackResponse));
        } catch (Exception e) {
            log.error("Failed to approve feedback with id: {}", feedbackId, e);
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error("Failed to approve feedback: " + e.getMessage()));
        }
    }

    @PostMapping("/{feedbackId}/response")
    @Operation(summary = "Add admin response (Admin)", description = "Add admin response to feedback")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<FeedbackResponse>> addAdminResponse(
            @PathVariable String feedbackId,
            @RequestParam String adminResponse) {
        try {
            Feedback feedback = feedbackService.addAdminResponse(feedbackService.findByFeedbackId(feedbackId).getId(), adminResponse);
            FeedbackResponse feedbackResponse = FeedbackResponse.fromEntity(feedback);
            return ResponseEntity.ok(ApiResponse.success("Admin response added successfully", feedbackResponse));
        } catch (Exception e) {
            log.error("Failed to add admin response to feedback with id: {}", feedbackId, e);
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error("Failed to add admin response: " + e.getMessage()));
        }
    }

    @DeleteMapping("/{feedbackId}")
    @Operation(summary = "Delete feedback (Admin)", description = "Delete a feedback")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<Void>> deleteFeedback(@PathVariable String feedbackId) {
        try {
            feedbackService.deleteFeedback(feedbackService.findByFeedbackId(feedbackId).getId());
            return ResponseEntity.ok(ApiResponse.success("Feedback deleted successfully", null));
        } catch (Exception e) {
            log.error("Failed to delete feedback with id: {}", feedbackId, e);
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error("Failed to delete feedback: " + e.getMessage()));
        }
    }

    @GetMapping("/stats")
    @Operation(summary = "Get feedback statistics (Admin)", description = "Get feedback statistics")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<FeedbackStatsResponse>> getFeedbackStats() {
        try {
            Double averageRating = feedbackService.getAverageRating();
            long approvedFeedback = feedbackService.countApprovedFeedback();
            long pendingFeedback = feedbackService.countPendingFeedback();
            List<Feedback> recentFeedback = feedbackService.findRecentApprovedFeedback(
                    org.springframework.data.domain.PageRequest.of(0, 5));
            List<Object[]> categoryStats = feedbackService.getFeedbackCountByCategory();
            
            FeedbackStatsResponse stats = new FeedbackStatsResponse();
            stats.setAverageRating(averageRating);
            stats.setTotalFeedback(approvedFeedback + pendingFeedback);
            stats.setApprovedFeedback(approvedFeedback);
            stats.setPendingFeedback(pendingFeedback);
            stats.setRecentFeedback(recentFeedback.stream().map(FeedbackResponse::fromEntity).toList());
            stats.setCategoryStats(categoryStats);
            
            return ResponseEntity.ok(ApiResponse.success("Feedback statistics retrieved", stats));
        } catch (Exception e) {
            log.error("Failed to get feedback statistics", e);
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error("Failed to get feedback statistics: " + e.getMessage()));
        }
    }

    public static class FeedbackStatsResponse {
        private Double averageRating;
        private long totalFeedback;
        private long approvedFeedback;
        private long pendingFeedback;
        private List<FeedbackResponse> recentFeedback;
        private List<Object[]> categoryStats;
        public FeedbackStatsResponse() {}
        public Double getAverageRating() { return averageRating; }
        public void setAverageRating(Double averageRating) { this.averageRating = averageRating; }
        public long getTotalFeedback() { return totalFeedback; }
        public void setTotalFeedback(long totalFeedback) { this.totalFeedback = totalFeedback; }
        public long getApprovedFeedback() { return approvedFeedback; }
        public void setApprovedFeedback(long approvedFeedback) { this.approvedFeedback = approvedFeedback; }
        public long getPendingFeedback() { return pendingFeedback; }
        public void setPendingFeedback(long pendingFeedback) { this.pendingFeedback = pendingFeedback; }
        public List<FeedbackResponse> getRecentFeedback() { return recentFeedback; }
        public void setRecentFeedback(List<FeedbackResponse> recentFeedback) { this.recentFeedback = recentFeedback; }
        public List<Object[]> getCategoryStats() { return categoryStats; }
        public void setCategoryStats(List<Object[]> categoryStats) { this.categoryStats = categoryStats; }
    }
}
