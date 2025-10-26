package com.lakgamana.entity;

import com.lakgamana.entity.enums.TrainType;
import com.lakgamana.entity.enums.TrainStatus;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "trains")
@EntityListeners(AuditingEntityListener.class)
public class Train {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "train_id", unique = true, nullable = false)
    private String trainId;

    @NotBlank(message = "Train name is required")
    @Size(max = 100, message = "Train name must not exceed 100 characters")
    @Column(name = "name", nullable = false)
    private String name;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TrainType type;

    @NotBlank(message = "Route is required")
    @Size(max = 200, message = "Route must not exceed 200 characters")
    @Column(nullable = false)
    private String route;

    @NotBlank(message = "From station is required")
    @Size(max = 100, message = "From station must not exceed 100 characters")
    @Column(name = "from_station", nullable = false)
    private String fromStation;

    @NotBlank(message = "To station is required")
    @Size(max = 100, message = "To station must not exceed 100 characters")
    @Column(name = "to_station", nullable = false)
    private String toStation;

    @NotNull(message = "Departure time is required")
    @Column(name = "departure_time", nullable = false)
    private LocalTime departureTime;

    @NotNull(message = "Arrival time is required")
    @Column(name = "arrival_time", nullable = false)
    private LocalTime arrivalTime;

    @NotBlank(message = "Duration is required")
    @Pattern(regexp = "^\\d+h\\s\\d+m$", message = "Duration must be in format 'Xh Ym'")
    @Column(nullable = false)
    private String duration;

    @NotBlank(message = "Distance is required")
    @Pattern(regexp = "^\\d+\\s*km$", message = "Distance must be in format 'X km'")
    @Column(nullable = false)
    private String distance;

    @Embedded
    @NotNull(message = "Pricing information is required")
    private Pricing pricing;

    @Embedded
    @NotNull(message = "Seat information is required")
    private SeatInfo seatInfo;

    @ElementCollection
    @CollectionTable(name = "train_features", joinColumns = @JoinColumn(name = "train_id"))
    @Column(name = "feature")
    private List<String> features = new ArrayList<>();

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TrainStatus status = TrainStatus.ACTIVE;

    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @LastModifiedDate
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @OneToMany(mappedBy = "train", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Booking> bookings = new ArrayList<>();

    @OneToMany(mappedBy = "train", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Feedback> feedbacks = new ArrayList<>();

    // Helper methods
    public boolean isActive() {
        return status == TrainStatus.ACTIVE;
    }

    public int getTotalAvailableSeats() {
        return seatInfo.getAvailableEconomy() + seatInfo.getAvailableBusiness() + seatInfo.getAvailableFirst();
    }

    public boolean hasAvailableSeats(String seatClass, int requestedSeats) {
        return switch (seatClass.toLowerCase()) {
            case "economy" -> seatInfo.getAvailableEconomy() >= requestedSeats;
            case "business" -> seatInfo.getAvailableBusiness() >= requestedSeats;
            case "first" -> seatInfo.getAvailableFirst() >= requestedSeats;
            default -> false;
        };
    }

    // Nested classes
    @Embeddable
    public static class Pricing {
        @Positive(message = "Economy price must be positive")
        @Column(name = "economy_price", nullable = false)
        private Integer economyPrice;

        @Positive(message = "Business price must be positive")
        @Column(name = "business_price", nullable = false)
        private Integer businessPrice;

        @Positive(message = "First class price must be positive")
        @Column(name = "first_price", nullable = false)
        private Integer firstPrice;

        // Default constructor
        public Pricing() {}

        // Constructor with parameters
        public Pricing(Integer economyPrice, Integer businessPrice, Integer firstPrice) {
            this.economyPrice = economyPrice;
            this.businessPrice = businessPrice;
            this.firstPrice = firstPrice;
        }

        // Getters and setters
        public Integer getEconomyPrice() {
            return economyPrice;
        }

        public void setEconomyPrice(Integer economyPrice) {
            this.economyPrice = economyPrice;
        }

        public Integer getBusinessPrice() {
            return businessPrice;
        }

        public void setBusinessPrice(Integer businessPrice) {
            this.businessPrice = businessPrice;
        }

        public Integer getFirstPrice() {
            return firstPrice;
        }

        public void setFirstPrice(Integer firstPrice) {
            this.firstPrice = firstPrice;
        }
    }

    @Embeddable
    public static class SeatInfo {
        @Positive(message = "Total seats must be positive")
        @Column(name = "total_seats", nullable = false)
        private Integer totalSeats;

        @Column(name = "available_economy", nullable = false)
        private Integer availableEconomy;

        @Column(name = "available_business", nullable = false)
        private Integer availableBusiness;

        @Column(name = "available_first", nullable = false)
        private Integer availableFirst;

        // Default constructor
        public SeatInfo() {}

        // Constructor with parameters
        public SeatInfo(Integer totalSeats, Integer availableEconomy, Integer availableBusiness, Integer availableFirst) {
            this.totalSeats = totalSeats;
            this.availableEconomy = availableEconomy;
            this.availableBusiness = availableBusiness;
            this.availableFirst = availableFirst;
        }

        // Getters and setters
        public Integer getTotalSeats() {
            return totalSeats;
        }

        public void setTotalSeats(Integer totalSeats) {
            this.totalSeats = totalSeats;
        }

        public Integer getAvailableEconomy() {
            return availableEconomy;
        }

        public void setAvailableEconomy(Integer availableEconomy) {
            this.availableEconomy = availableEconomy;
        }

        public Integer getAvailableBusiness() {
            return availableBusiness;
        }

        public void setAvailableBusiness(Integer availableBusiness) {
            this.availableBusiness = availableBusiness;
        }

        public Integer getAvailableFirst() {
            return availableFirst;
        }

        public void setAvailableFirst(Integer availableFirst) {
            this.availableFirst = availableFirst;
        }
    }

    // Default constructor
    public Train() {}

    // Getters and setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTrainId() {
        return trainId;
    }

    public void setTrainId(String trainId) {
        this.trainId = trainId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public TrainType getType() {
        return type;
    }

    public void setType(TrainType type) {
        this.type = type;
    }

    public String getRoute() {
        return route;
    }

    public void setRoute(String route) {
        this.route = route;
    }

    public String getFromStation() {
        return fromStation;
    }

    public void setFromStation(String fromStation) {
        this.fromStation = fromStation;
    }

    public String getToStation() {
        return toStation;
    }

    public void setToStation(String toStation) {
        this.toStation = toStation;
    }

    public LocalTime getDepartureTime() {
        return departureTime;
    }

    public void setDepartureTime(LocalTime departureTime) {
        this.departureTime = departureTime;
    }

    public LocalTime getArrivalTime() {
        return arrivalTime;
    }

    public void setArrivalTime(LocalTime arrivalTime) {
        this.arrivalTime = arrivalTime;
    }

    public String getDuration() {
        return duration;
    }

    public void setDuration(String duration) {
        this.duration = duration;
    }

    public String getDistance() {
        return distance;
    }

    public void setDistance(String distance) {
        this.distance = distance;
    }

    public Pricing getPricing() {
        return pricing;
    }

    public void setPricing(Pricing pricing) {
        this.pricing = pricing;
    }

    public SeatInfo getSeatInfo() {
        return seatInfo;
    }

    public void setSeatInfo(SeatInfo seatInfo) {
        this.seatInfo = seatInfo;
    }

    public List<String> getFeatures() {
        return features;
    }

    public void setFeatures(List<String> features) {
        this.features = features;
    }

    public TrainStatus getStatus() {
        return status;
    }

    public void setStatus(TrainStatus status) {
        this.status = status;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    public List<Booking> getBookings() {
        return bookings;
    }

    public void setBookings(List<Booking> bookings) {
        this.bookings = bookings;
    }

    public List<Feedback> getFeedbacks() {
        return feedbacks;
    }

    public void setFeedbacks(List<Feedback> feedbacks) {
        this.feedbacks = feedbacks;
    }
}
