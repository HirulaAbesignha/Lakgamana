package com.lakgamana.service;

import com.lakgamana.dto.request.TrainSearchRequest;
import com.lakgamana.dto.response.TrainResponse;
import com.lakgamana.entity.Train;
import com.lakgamana.entity.enums.TrainType;
import com.lakgamana.entity.enums.TrainStatus;
import com.lakgamana.repository.TrainRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TrainServiceTest {

    @Mock
    private TrainRepository trainRepository;

    @InjectMocks
    private TrainService trainService;

    private Train testTrain;
    private TrainSearchRequest searchRequest;

    @BeforeEach
    void setUp() {
        Map<String, Integer> prices = new HashMap<>();
        prices.put("ECONOMY", 1200);
        prices.put("BUSINESS", 2500);
        prices.put("FIRST", 3500);

        Map<String, Integer> seats = new HashMap<>();
        seats.put("ECONOMY", 150);
        seats.put("BUSINESS", 30);
        seats.put("FIRST", 20);

        testTrain = new Train();
        testTrain.setId(1L);
        testTrain.setName("Intercity Express");
        testTrain.setType(TrainType.EXPRESS);
        testTrain.setRoute("Colombo Fort - Kandy");
        testTrain.setFromStation("Colombo Fort");
        testTrain.setToStation("Kandy");
        testTrain.setDepartureTime(LocalTime.of(8, 30));
        testTrain.setArrivalTime(LocalTime.of(11, 45));
        testTrain.setDuration("3h 15m");
        testTrain.setDistance("120 km");
        testTrain.setPrice(prices);
        testTrain.setSeats(seats);
        testTrain.setStatus(TrainStatus.ACTIVE);
        testTrain.setFeatures(Arrays.asList("WiFi", "AC", "Food Service"));

        searchRequest = new TrainSearchRequest();
        searchRequest.setFrom("Colombo Fort");
        searchRequest.setTo("Kandy");
        searchRequest.setDate(LocalDate.of(2024, 12, 15));
        searchRequest.setAdults(2);
        searchRequest.setChildren(1);
    }

    @Test
    void getAllTrains_ShouldReturnAllTrains_WhenTrainsExist() {
        when(trainRepository.findAll()).thenReturn(Arrays.asList(testTrain));

        List<TrainResponse> response = trainService.getAllTrains();

        assertNotNull(response);
        assertEquals(1, response.size());
        assertEquals("Intercity Express", response.get(0).getName());
        assertEquals("EXPRESS", response.get(0).getType());

        verify(trainRepository).findAll();
    }

    @Test
    void getTrainById_ShouldReturnTrain_WhenTrainExists() {
        when(trainRepository.findById(1L)).thenReturn(Optional.of(testTrain));

        TrainResponse response = trainService.getTrainById(1L);

        assertNotNull(response);
        assertEquals("Intercity Express", response.getName());
        assertEquals("Colombo Fort", response.getFromStation());
        assertEquals("Kandy", response.getToStation());

        verify(trainRepository).findById(1L);
    }

    @Test
    void getTrainById_ShouldThrowException_WhenTrainNotFound() {
        when(trainRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> trainService.getTrainById(1L));

        verify(trainRepository).findById(1L);
    }

    @Test
    void searchTrains_ShouldReturnMatchingTrains_WhenSearchCriteriaMatch() {
        when(trainRepository.findAll()).thenReturn(Arrays.asList(testTrain));

        List<TrainResponse> response = trainService.searchTrains(searchRequest);

        assertNotNull(response);
        assertEquals(1, response.size());
        assertEquals("Intercity Express", response.get(0).getName());

        verify(trainRepository).findAll();
    }

    @Test
    void searchTrains_ShouldReturnEmptyList_WhenNoMatchingTrains() {
        searchRequest.setFrom("Galle");
        searchRequest.setTo("Matara");

        when(trainRepository.findAll()).thenReturn(Arrays.asList(testTrain));

        List<TrainResponse> response = trainService.searchTrains(searchRequest);

        assertNotNull(response);
        assertEquals(0, response.size());

        verify(trainRepository).findAll();
    }

    @Test
    void createTrain_ShouldReturnCreatedTrain_WhenValidRequest() {
        when(trainRepository.save(any(Train.class))).thenReturn(testTrain);

        TrainResponse response = trainService.createTrain(testTrain);

        assertNotNull(response);
        assertEquals("Intercity Express", response.getName());

        verify(trainRepository).save(any(Train.class));
    }

    @Test
    void updateTrain_ShouldReturnUpdatedTrain_WhenTrainExists() {
        when(trainRepository.findById(1L)).thenReturn(Optional.of(testTrain));
        when(trainRepository.save(any(Train.class))).thenReturn(testTrain);

        TrainResponse response = trainService.updateTrain(1L, testTrain);

        assertNotNull(response);
        assertEquals("Intercity Express", response.getName());

        verify(trainRepository).findById(1L);
        verify(trainRepository).save(any(Train.class));
    }

    @Test
    void deleteTrain_ShouldDeleteTrain_WhenTrainExists() {
        when(trainRepository.existsById(1L)).thenReturn(true);

        trainService.deleteTrain(1L);

        verify(trainRepository).existsById(1L);
        verify(trainRepository).deleteById(1L);
    }

    @Test
    void deleteTrain_ShouldThrowException_WhenTrainNotFound() {
        when(trainRepository.existsById(1L)).thenReturn(false);

        assertThrows(RuntimeException.class, () -> trainService.deleteTrain(1L));

        verify(trainRepository).existsById(1L);
        verify(trainRepository, never()).deleteById(any());
    }
}
