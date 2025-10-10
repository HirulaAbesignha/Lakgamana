package com.lakgamana.service;

import com.lakgamana.dto.request.TrainSearchRequest;
import com.lakgamana.entity.Train;
import com.lakgamana.entity.enums.TrainType;
import com.lakgamana.entity.enums.TrainStatus;
import com.lakgamana.repository.TrainRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class TrainService {

    private final TrainRepository trainRepository;

    @Transactional(readOnly = true)
    public List<Train> findAvailableTrains(TrainSearchRequest searchRequest) {
        return trainRepository.findAvailableTrains(
                searchRequest.getFrom(),
                searchRequest.getTo(),
                searchRequest.getDate().equals(java.time.LocalDate.now()) ? LocalTime.now() : null
        );
    }

    @Transactional(readOnly = true)
    public Train findById(Long id) {
        return trainRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Train not found with id: " + id));
    }

    @Transactional(readOnly = true)
    public Train findByTrainId(String trainId) {
        return trainRepository.findByTrainId(trainId)
                .orElseThrow(() -> new RuntimeException("Train not found with trainId: " + trainId));
    }

    @Transactional(readOnly = true)
    public Page<Train> findTrainsWithFilters(String fromStation, String toStation, 
                                           TrainType type, TrainStatus status, 
                                           Pageable pageable) {
        return trainRepository.findTrainsWithFilters(fromStation, toStation, type, status, pageable);
    }

    @Transactional(readOnly = true)
    public List<Train> findAllTrains() {
        return trainRepository.findAll();
    }

    @Transactional(readOnly = true)
    public List<Train> findByStatus(TrainStatus status) {
        return trainRepository.findByStatus(status);
    }

    @Transactional(readOnly = true)
    public List<Train> findByType(TrainType type) {
        return trainRepository.findByType(type);
    }

    @Transactional(readOnly = true)
    public List<String> getAllFromStations() {
        return trainRepository.findAllFromStations();
    }

    @Transactional(readOnly = true)
    public List<String> getAllToStations() {
        return trainRepository.findAllToStations();
    }

    public Train createTrain(Train train) {
        if (train.getTrainId() == null || train.getTrainId().isEmpty()) {
            train.setTrainId(generateTrainId());
        }
        
        train.setCreatedAt(LocalDateTime.now());
        train.setUpdatedAt(LocalDateTime.now());
        
        return trainRepository.save(train);
    }

    public Train updateTrain(Long id, Train trainDetails) {
        Train train = findById(id);
        
        train.setName(trainDetails.getName());
        train.setType(trainDetails.getType());
        train.setRoute(trainDetails.getRoute());
        train.setFromStation(trainDetails.getFromStation());
        train.setToStation(trainDetails.getToStation());
        train.setDepartureTime(trainDetails.getDepartureTime());
        train.setArrivalTime(trainDetails.getArrivalTime());
        train.setDuration(trainDetails.getDuration());
        train.setDistance(trainDetails.getDistance());
        train.setPricing(trainDetails.getPricing());
        train.setSeatInfo(trainDetails.getSeatInfo());
        train.setFeatures(trainDetails.getFeatures());
        train.setStatus(trainDetails.getStatus());
        train.setUpdatedAt(LocalDateTime.now());
        
        return trainRepository.save(train);
    }

    public void deleteTrain(Long id) {
        Train train = findById(id);
        trainRepository.delete(train);
    }

    @Transactional(readOnly = true)
    public long countActiveTrains() {
        return trainRepository.countActiveTrains();
    }

    @Transactional(readOnly = true)
    public List<Train> findRecentActiveTrains(Pageable pageable) {
        return trainRepository.findRecentActiveTrains(pageable);
    }

    public boolean hasAvailableSeats(Long trainId, String seatClass, int requestedSeats) {
        Train train = findById(trainId);
        return train.hasAvailableSeats(seatClass, requestedSeats);
    }

    public void updateSeatAvailability(Long trainId, String seatClass, int bookedSeats) {
        Train train = findById(trainId);
        Train.SeatInfo seatInfo = train.getSeatInfo();
        
        switch (seatClass.toLowerCase()) {
            case "economy":
                seatInfo.setAvailableEconomy(seatInfo.getAvailableEconomy() - bookedSeats);
                break;
            case "business":
                seatInfo.setAvailableBusiness(seatInfo.getAvailableBusiness() - bookedSeats);
                break;
            case "first":
                seatInfo.setAvailableFirst(seatInfo.getAvailableFirst() - bookedSeats);
                break;
            default:
                throw new RuntimeException("Invalid seat class: " + seatClass);
        }
        
        train.setSeatInfo(seatInfo);
        train.setUpdatedAt(LocalDateTime.now());
        trainRepository.save(train);
    }

    private String generateTrainId() {
        String trainId;
        do {
            trainId = "T" + String.format("%03d", System.currentTimeMillis() % 1000);
        } while (trainRepository.findByTrainId(trainId).isPresent());
        return trainId;
    }
}
