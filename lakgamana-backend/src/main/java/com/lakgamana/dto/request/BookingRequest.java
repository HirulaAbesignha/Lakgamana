package com.lakgamana.dto.request;

import com.lakgamana.dto.request.PassengerRequest;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
 

import java.time.LocalDate;
import java.util.List;

public class BookingRequest {

    @NotNull(message = "Train ID is required")
    private Long trainId;

    @NotNull(message = "Departure date is required")
    private LocalDate departureDate;

    @NotBlank(message = "Seat class is required")
    private String seatClass;

    @Valid
    @NotNull(message = "Passengers information is required")
    private List<PassengerRequest> passengers;

    @Positive(message = "Adults count must be positive")
    private Integer adultsCount = 1;

    private Integer childrenCount = 0;

    @Positive(message = "Total amount must be positive")
    private Double totalAmount;

    public BookingRequest() {}

    public Long getTrainId() { return trainId; }
    public void setTrainId(Long trainId) { this.trainId = trainId; }
    public LocalDate getDepartureDate() { return departureDate; }
    public void setDepartureDate(LocalDate departureDate) { this.departureDate = departureDate; }
    public String getSeatClass() { return seatClass; }
    public void setSeatClass(String seatClass) { this.seatClass = seatClass; }
    public List<PassengerRequest> getPassengers() { return passengers; }
    public void setPassengers(List<PassengerRequest> passengers) { this.passengers = passengers; }
    public Integer getAdultsCount() { return adultsCount; }
    public void setAdultsCount(Integer adultsCount) { this.adultsCount = adultsCount; }
    public Integer getChildrenCount() { return childrenCount; }
    public void setChildrenCount(Integer childrenCount) { this.childrenCount = childrenCount; }
    public Double getTotalAmount() { return totalAmount; }
    public void setTotalAmount(Double totalAmount) { this.totalAmount = totalAmount; }
}
