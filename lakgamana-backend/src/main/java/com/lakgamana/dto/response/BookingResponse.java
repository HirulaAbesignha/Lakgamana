package com.lakgamana.dto.response;

import com.lakgamana.entity.Booking;
import com.lakgamana.entity.enums.BookingStatus;
 

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

public class BookingResponse {

    private Long id;
    private String bookingId;
    private UserResponse user;
    private TrainResponse train;
    private LocalDate departureDate;
    private LocalTime departureTime;
    private LocalTime arrivalTime;
    private String seatClass;
    private String seatNumber;
    private Double totalAmount;
    private BookingStatus status;
    private LocalDateTime bookingDate;
    private LocalDateTime cancellationDate;
    private String cancellationReason;
    private List<PassengerResponse> passengers;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

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
        private String fromStation;
        private String toStation;
        public TrainResponse() {}
        public Long getId() { return id; }
        public void setId(Long id) { this.id = id; }
        public String getTrainId() { return trainId; }
        public void setTrainId(String trainId) { this.trainId = trainId; }
        public String getName() { return name; }
        public void setName(String name) { this.name = name; }
        public String getRoute() { return route; }
        public void setRoute(String route) { this.route = route; }
        public String getFromStation() { return fromStation; }
        public void setFromStation(String fromStation) { this.fromStation = fromStation; }
        public String getToStation() { return toStation; }
        public void setToStation(String toStation) { this.toStation = toStation; }
    }

    public static class PassengerResponse {
        private Long id;
        private String name;
        private Integer age;
        private String gender;
        private String idType;
        private String idNumber;
        public PassengerResponse() {}
        public Long getId() { return id; }
        public void setId(Long id) { this.id = id; }
        public String getName() { return name; }
        public void setName(String name) { this.name = name; }
        public Integer getAge() { return age; }
        public void setAge(Integer age) { this.age = age; }
        public String getGender() { return gender; }
        public void setGender(String gender) { this.gender = gender; }
        public String getIdType() { return idType; }
        public void setIdType(String idType) { this.idType = idType; }
        public String getIdNumber() { return idNumber; }
        public void setIdNumber(String idNumber) { this.idNumber = idNumber; }
    }

    public BookingResponse() {}
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getBookingId() { return bookingId; }
    public void setBookingId(String bookingId) { this.bookingId = bookingId; }
    public UserResponse getUser() { return user; }
    public void setUser(UserResponse user) { this.user = user; }
    public TrainResponse getTrain() { return train; }
    public void setTrain(TrainResponse train) { this.train = train; }
    public LocalDate getDepartureDate() { return departureDate; }
    public void setDepartureDate(LocalDate departureDate) { this.departureDate = departureDate; }
    public LocalTime getDepartureTime() { return departureTime; }
    public void setDepartureTime(LocalTime departureTime) { this.departureTime = departureTime; }
    public LocalTime getArrivalTime() { return arrivalTime; }
    public void setArrivalTime(LocalTime arrivalTime) { this.arrivalTime = arrivalTime; }
    public String getSeatClass() { return seatClass; }
    public void setSeatClass(String seatClass) { this.seatClass = seatClass; }
    public String getSeatNumber() { return seatNumber; }
    public void setSeatNumber(String seatNumber) { this.seatNumber = seatNumber; }
    public Double getTotalAmount() { return totalAmount; }
    public void setTotalAmount(Double totalAmount) { this.totalAmount = totalAmount; }
    public BookingStatus getStatus() { return status; }
    public void setStatus(BookingStatus status) { this.status = status; }
    public LocalDateTime getBookingDate() { return bookingDate; }
    public void setBookingDate(LocalDateTime bookingDate) { this.bookingDate = bookingDate; }
    public LocalDateTime getCancellationDate() { return cancellationDate; }
    public void setCancellationDate(LocalDateTime cancellationDate) { this.cancellationDate = cancellationDate; }
    public String getCancellationReason() { return cancellationReason; }
    public void setCancellationReason(String cancellationReason) { this.cancellationReason = cancellationReason; }
    public List<PassengerResponse> getPassengers() { return passengers; }
    public void setPassengers(List<PassengerResponse> passengers) { this.passengers = passengers; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }

    public static BookingResponse fromEntity(Booking booking) {
        BookingResponse res = new BookingResponse();
        res.setId(booking.getId());
        res.setBookingId(booking.getBookingId());
        UserResponse ur = new UserResponse();
        ur.setId(booking.getUser().getId());
        ur.setUserId(booking.getUser().getUserId());
        ur.setFirstName(booking.getUser().getFirstName());
        ur.setLastName(booking.getUser().getLastName());
        ur.setEmail(booking.getUser().getEmail());
        res.setUser(ur);
        TrainResponse tr = new TrainResponse();
        tr.setId(booking.getTrain().getId());
        tr.setTrainId(booking.getTrain().getTrainId());
        tr.setName(booking.getTrain().getName());
        tr.setRoute(booking.getTrain().getRoute());
        tr.setFromStation(booking.getTrain().getFromStation());
        tr.setToStation(booking.getTrain().getToStation());
        res.setTrain(tr);
        res.setDepartureDate(booking.getDepartureDate());
        res.setDepartureTime(booking.getDepartureTime());
        res.setArrivalTime(booking.getArrivalTime());
        res.setSeatClass(booking.getSeatClass());
        res.setSeatNumber(booking.getSeatNumber());
        res.setTotalAmount(booking.getTotalAmount());
        res.setStatus(booking.getStatus());
        res.setBookingDate(booking.getBookingDate());
        res.setCancellationDate(booking.getCancellationDate());
        res.setCancellationReason(booking.getCancellationReason());
        List<PassengerResponse> prs = booking.getPassengers().stream().map(passenger -> {
            PassengerResponse pr = new PassengerResponse();
            pr.setId(passenger.getId());
            pr.setName(passenger.getName());
            pr.setAge(passenger.getAge());
            pr.setGender(passenger.getGender().name());
            pr.setIdType(passenger.getIdType().name());
            pr.setIdNumber(passenger.getIdNumber());
            return pr;
        }).toList();
        res.setPassengers(prs);
        res.setCreatedAt(booking.getCreatedAt());
        res.setUpdatedAt(booking.getUpdatedAt());
        return res;
    }
}
