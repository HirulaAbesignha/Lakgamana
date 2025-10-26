package com.lakgamana.dto.response;

import com.lakgamana.entity.Train;
import com.lakgamana.entity.enums.TrainType;
import com.lakgamana.entity.enums.TrainStatus;
 

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

public class TrainResponse {

    private Long id;
    private String trainId;
    private String name;
    private TrainType type;
    private String route;
    private String fromStation;
    private String toStation;
    private LocalTime departureTime;
    private LocalTime arrivalTime;
    private String duration;
    private String distance;
    private PricingResponse pricing;
    private SeatInfoResponse seatInfo;
    private List<String> features;
    private TrainStatus status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public static class PricingResponse {
        private Integer economyPrice;
        private Integer businessPrice;
        private Integer firstPrice;
        public PricingResponse() {}
        public Integer getEconomyPrice() { return economyPrice; }
        public void setEconomyPrice(Integer economyPrice) { this.economyPrice = economyPrice; }
        public Integer getBusinessPrice() { return businessPrice; }
        public void setBusinessPrice(Integer businessPrice) { this.businessPrice = businessPrice; }
        public Integer getFirstPrice() { return firstPrice; }
        public void setFirstPrice(Integer firstPrice) { this.firstPrice = firstPrice; }
    }

    public static class SeatInfoResponse {
        private Integer totalSeats;
        private Integer availableEconomy;
        private Integer availableBusiness;
        private Integer availableFirst;
        public SeatInfoResponse() {}
        public Integer getTotalSeats() { return totalSeats; }
        public void setTotalSeats(Integer totalSeats) { this.totalSeats = totalSeats; }
        public Integer getAvailableEconomy() { return availableEconomy; }
        public void setAvailableEconomy(Integer availableEconomy) { this.availableEconomy = availableEconomy; }
        public Integer getAvailableBusiness() { return availableBusiness; }
        public void setAvailableBusiness(Integer availableBusiness) { this.availableBusiness = availableBusiness; }
        public Integer getAvailableFirst() { return availableFirst; }
        public void setAvailableFirst(Integer availableFirst) { this.availableFirst = availableFirst; }
    }

    public TrainResponse() {}
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getTrainId() { return trainId; }
    public void setTrainId(String trainId) { this.trainId = trainId; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public TrainType getType() { return type; }
    public void setType(TrainType type) { this.type = type; }
    public String getRoute() { return route; }
    public void setRoute(String route) { this.route = route; }
    public String getFromStation() { return fromStation; }
    public void setFromStation(String fromStation) { this.fromStation = fromStation; }
    public String getToStation() { return toStation; }
    public void setToStation(String toStation) { this.toStation = toStation; }
    public LocalTime getDepartureTime() { return departureTime; }
    public void setDepartureTime(LocalTime departureTime) { this.departureTime = departureTime; }
    public LocalTime getArrivalTime() { return arrivalTime; }
    public void setArrivalTime(LocalTime arrivalTime) { this.arrivalTime = arrivalTime; }
    public String getDuration() { return duration; }
    public void setDuration(String duration) { this.duration = duration; }
    public String getDistance() { return distance; }
    public void setDistance(String distance) { this.distance = distance; }
    public PricingResponse getPricing() { return pricing; }
    public void setPricing(PricingResponse pricing) { this.pricing = pricing; }
    public SeatInfoResponse getSeatInfo() { return seatInfo; }
    public void setSeatInfo(SeatInfoResponse seatInfo) { this.seatInfo = seatInfo; }
    public List<String> getFeatures() { return features; }
    public void setFeatures(List<String> features) { this.features = features; }
    public TrainStatus getStatus() { return status; }
    public void setStatus(TrainStatus status) { this.status = status; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }

    public static TrainResponse fromEntity(Train train) {
        TrainResponse res = new TrainResponse();
        res.setId(train.getId());
        res.setTrainId(train.getTrainId());
        res.setName(train.getName());
        res.setType(train.getType());
        res.setRoute(train.getRoute());
        res.setFromStation(train.getFromStation());
        res.setToStation(train.getToStation());
        res.setDepartureTime(train.getDepartureTime());
        res.setArrivalTime(train.getArrivalTime());
        res.setDuration(train.getDuration());
        res.setDistance(train.getDistance());
        PricingResponse pricing = new PricingResponse();
        if (train.getPricing() != null) {
            pricing.setEconomyPrice(train.getPricing().getEconomyPrice());
            pricing.setBusinessPrice(train.getPricing().getBusinessPrice());
            pricing.setFirstPrice(train.getPricing().getFirstPrice());
            res.setPricing(pricing);
        }
        SeatInfoResponse seat = new SeatInfoResponse();
        if (train.getSeatInfo() != null) {
            seat.setTotalSeats(train.getSeatInfo().getTotalSeats());
            seat.setAvailableEconomy(train.getSeatInfo().getAvailableEconomy());
            seat.setAvailableBusiness(train.getSeatInfo().getAvailableBusiness());
            seat.setAvailableFirst(train.getSeatInfo().getAvailableFirst());
            res.setSeatInfo(seat);
        }
        res.setFeatures(train.getFeatures());
        res.setStatus(train.getStatus());
        res.setCreatedAt(train.getCreatedAt());
        res.setUpdatedAt(train.getUpdatedAt());
        return res;
    }
}
