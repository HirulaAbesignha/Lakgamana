package com.lakgamana.entity;

import com.lakgamana.entity.enums.TrainStatus;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@Entity
@Table(name = "routes")
@EntityListeners(AuditingEntityListener.class)
public class Route {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "route_id", unique = true, nullable = false)
    private String routeId;

    @NotBlank
    @Size(max = 100)
    @Column(nullable = false)
    private String name;

    @NotBlank
    @Size(max = 100)
    @Column(name = "from_station", nullable = false)
    private String fromStation;

    @NotBlank
    @Size(max = 100)
    @Column(name = "to_station", nullable = false)
    private String toStation;

    @NotBlank
    @Column(nullable = false)
    private String distance;

    @NotBlank
    @Column(nullable = false)
    private String duration;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TrainStatus status = TrainStatus.ACTIVE;

    @Embedded
    private Schedule schedule = new Schedule();

    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @LastModifiedDate
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @Embeddable
    public static class Schedule {
        @Column(name = "monday")
        private boolean monday = true;
        @Column(name = "tuesday")
        private boolean tuesday = true;
        @Column(name = "wednesday")
        private boolean wednesday = true;
        @Column(name = "thursday")
        private boolean thursday = true;
        @Column(name = "friday")
        private boolean friday = true;
        @Column(name = "saturday")
        private boolean saturday = true;
        @Column(name = "sunday")
        private boolean sunday = true;

        public Schedule() {}

        public boolean isMonday() { return monday; }
        public void setMonday(boolean monday) { this.monday = monday; }
        public boolean isTuesday() { return tuesday; }
        public void setTuesday(boolean tuesday) { this.tuesday = tuesday; }
        public boolean isWednesday() { return wednesday; }
        public void setWednesday(boolean wednesday) { this.wednesday = wednesday; }
        public boolean isThursday() { return thursday; }
        public void setThursday(boolean thursday) { this.thursday = thursday; }
        public boolean isFriday() { return friday; }
        public void setFriday(boolean friday) { this.friday = friday; }
        public boolean isSaturday() { return saturday; }
        public void setSaturday(boolean saturday) { this.saturday = saturday; }
        public boolean isSunday() { return sunday; }
        public void setSunday(boolean sunday) { this.sunday = sunday; }
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getRouteId() { return routeId; }
    public void setRouteId(String routeId) { this.routeId = routeId; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getFromStation() { return fromStation; }
    public void setFromStation(String fromStation) { this.fromStation = fromStation; }
    public String getToStation() { return toStation; }
    public void setToStation(String toStation) { this.toStation = toStation; }
    public String getDistance() { return distance; }
    public void setDistance(String distance) { this.distance = distance; }
    public String getDuration() { return duration; }
    public void setDuration(String duration) { this.duration = duration; }
    public TrainStatus getStatus() { return status; }
    public void setStatus(TrainStatus status) { this.status = status; }
    public Schedule getSchedule() { return schedule; }
    public void setSchedule(Schedule schedule) { this.schedule = schedule; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
}


