package com.lakgamana.dto.response;

import com.lakgamana.entity.Feedback;
import com.lakgamana.entity.enums.FeedbackCategory;
import com.lakgamana.entity.enums.FeedbackStatus;
 

import java.time.LocalDateTime;

public class FeedbackResponse {

    private Long id;
    private String feedbackId;
    private UserResponse user;
    private String bookingId;
    private TrainResponse train;
    private Integer rating;
    private String title;
    private String comment;
    private FeedbackCategory category;
    private FeedbackStatus status;
    private LocalDateTime submittedDate;
    private String adminResponse;
    private LocalDateTime adminResponseDate;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    // Default constructor
    public FeedbackResponse() {}

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getFeedbackId() { return feedbackId; }
    public void setFeedbackId(String feedbackId) { this.feedbackId = feedbackId; }
    public UserResponse getUser() { return user; }
    public void setUser(UserResponse user) { this.user = user; }
    public String getBookingId() { return bookingId; }
    public void setBookingId(String bookingId) { this.bookingId = bookingId; }
    public TrainResponse getTrain() { return train; }
    public void setTrain(TrainResponse train) { this.train = train; }
    public Integer getRating() { return rating; }
    public void setRating(Integer rating) { this.rating = rating; }
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public String getComment() { return comment; }
    public void setComment(String comment) { this.comment = comment; }
    public FeedbackCategory getCategory() { return category; }
    public void setCategory(FeedbackCategory category) { this.category = category; }
    public FeedbackStatus getStatus() { return status; }
    public void setStatus(FeedbackStatus status) { this.status = status; }
    public LocalDateTime getSubmittedDate() { return submittedDate; }
    public void setSubmittedDate(LocalDateTime submittedDate) { this.submittedDate = submittedDate; }
    public String getAdminResponse() { return adminResponse; }
    public void setAdminResponse(String adminResponse) { this.adminResponse = adminResponse; }
    public LocalDateTime getAdminResponseDate() { return adminResponseDate; }
    public void setAdminResponseDate(LocalDateTime adminResponseDate) { this.adminResponseDate = adminResponseDate; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }

    public static class UserResponse {
        private Long id;
        private String userId;
        private String firstName;
        private String lastName;
        private String email;
        public UserResponse() {}
        public Long getId() { return id; }
        public void setId(Long id) { this.id = id; }
        public String getUserId() { return userId; }
        public void setUserId(String userId) { this.userId = userId; }
        public String getFirstName() { return firstName; }
        public void setFirstName(String firstName) { this.firstName = firstName; }
        public String getLastName() { return lastName; }
        public void setLastName(String lastName) { this.lastName = lastName; }
        public String getEmail() { return email; }
        public void setEmail(String email) { this.email = email; }
    }

    public static class TrainResponse {
        private Long id;
        private String trainId;
        private String name;
        private String route;
        public TrainResponse() {}
        public Long getId() { return id; }
        public void setId(Long id) { this.id = id; }
        public String getTrainId() { return trainId; }
        public void setTrainId(String trainId) { this.trainId = trainId; }
        public String getName() { return name; }
        public void setName(String name) { this.name = name; }
        public String getRoute() { return route; }
        public void setRoute(String route) { this.route = route; }
    }

    public static FeedbackResponse fromEntity(Feedback feedback) {
        FeedbackResponse res = new FeedbackResponse();
        res.id = feedback.getId();
        res.feedbackId = feedback.getFeedbackId();
        UserResponse ur = new UserResponse();
        ur.setId(feedback.getUser().getId());
        ur.setUserId(feedback.getUser().getUserId());
        ur.setFirstName(feedback.getUser().getFirstName());
        ur.setLastName(feedback.getUser().getLastName());
        ur.setEmail(feedback.getUser().getEmail());
        res.user = ur;
        res.bookingId = feedback.getBooking() != null ? feedback.getBooking().getBookingId() : null;
        if (feedback.getTrain() != null) {
            TrainResponse tr = new TrainResponse();
            tr.setId(feedback.getTrain().getId());
            tr.setTrainId(feedback.getTrain().getTrainId());
            tr.setName(feedback.getTrain().getName());
            tr.setRoute(feedback.getTrain().getRoute());
            res.train = tr;
        }
        res.rating = feedback.getRating();
        res.title = feedback.getTitle();
        res.comment = feedback.getComment();
        res.category = feedback.getCategory();
        res.status = feedback.getStatus();
        res.submittedDate = feedback.getSubmittedDate();
        res.adminResponse = feedback.getAdminResponse();
        res.adminResponseDate = feedback.getAdminResponseDate();
        res.createdAt = feedback.getCreatedAt();
        res.updatedAt = feedback.getUpdatedAt();
        return res;
    }
}
