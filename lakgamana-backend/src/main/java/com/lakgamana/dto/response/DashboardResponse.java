package com.lakgamana.dto.response;

import java.util.List;

public class DashboardResponse {

    private DashboardStats stats;
    private List<BookingResponse> recentBookings;
    private List<FeedbackResponse> recentFeedback;

    public DashboardResponse() {
    }

    public DashboardStats getStats() {
        return stats;
    }

    public void setStats(DashboardStats stats) {
        this.stats = stats;
    }

    public List<BookingResponse> getRecentBookings() {
        return recentBookings;
    }

    public void setRecentBookings(List<BookingResponse> recentBookings) {
        this.recentBookings = recentBookings;
    }

    public List<FeedbackResponse> getRecentFeedback() {
        return recentFeedback;
    }

    public void setRecentFeedback(List<FeedbackResponse> recentFeedback) {
        this.recentFeedback = recentFeedback;
    }

    public static class DashboardStats {
        private Long totalTrains;
        private Long totalReservations;
        private Long totalUsers;
        private Double totalRevenue;
        private Long confirmedBookings;
        private Long cancelledBookings;
        private Long pendingPayments;

        public DashboardStats() {
        }

        public Long getTotalTrains() {
            return totalTrains;
        }

        public void setTotalTrains(Long totalTrains) {
            this.totalTrains = totalTrains;
        }

        public Long getTotalReservations() {
            return totalReservations;
        }

        public void setTotalReservations(Long totalReservations) {
            this.totalReservations = totalReservations;
        }

        public Long getTotalUsers() {
            return totalUsers;
        }

        public void setTotalUsers(Long totalUsers) {
            this.totalUsers = totalUsers;
        }

        public Double getTotalRevenue() {
            return totalRevenue;
        }

        public void setTotalRevenue(Double totalRevenue) {
            this.totalRevenue = totalRevenue;
        }

        public Long getConfirmedBookings() {
            return confirmedBookings;
        }

        public void setConfirmedBookings(Long confirmedBookings) {
            this.confirmedBookings = confirmedBookings;
        }

        public Long getCancelledBookings() {
            return cancelledBookings;
        }

        public void setCancelledBookings(Long cancelledBookings) {
            this.cancelledBookings = cancelledBookings;
        }

        public Long getPendingPayments() {
            return pendingPayments;
        }

        public void setPendingPayments(Long pendingPayments) {
            this.pendingPayments = pendingPayments;
        }
    }
}
