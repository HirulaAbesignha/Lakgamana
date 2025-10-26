package com.lakgamana.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.lakgamana.dto.request.TrainSearchRequest;
import com.lakgamana.dto.response.TrainResponse;
import com.lakgamana.service.TrainService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(TrainController.class)
class TrainControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private TrainService trainService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void getAllTrains_ShouldReturnSuccess_WhenTrainsExist() throws Exception {
        Map<String, Integer> prices = new HashMap<>();
        prices.put("ECONOMY", 1200);
        prices.put("BUSINESS", 2500);
        prices.put("FIRST", 3500);

        Map<String, Integer> seats = new HashMap<>();
        seats.put("ECONOMY", 150);
        seats.put("BUSINESS", 30);
        seats.put("FIRST", 20);

        TrainResponse train = TrainResponse.builder()
                .id(1L)
                .name("Intercity Express")
                .type("EXPRESS")
                .route("Colombo Fort - Kandy")
                .fromStation("Colombo Fort")
                .toStation("Kandy")
                .departureTime(LocalTime.of(8, 30))
                .arrivalTime(LocalTime.of(11, 45))
                .duration("3h 15m")
                .distance("120 km")
                .price(prices)
                .seats(seats)
                .status("ACTIVE")
                .features(Arrays.asList("WiFi", "AC", "Food Service"))
                .build();

        when(trainService.getAllTrains()).thenReturn(Arrays.asList(train));

        mockMvc.perform(get("/api/trains"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data[0].name").value("Intercity Express"))
                .andExpect(jsonPath("$.data[0].type").value("EXPRESS"));
    }

    @Test
    void getTrainById_ShouldReturnSuccess_WhenTrainExists() throws Exception {
        Map<String, Integer> prices = new HashMap<>();
        prices.put("ECONOMY", 1200);
        prices.put("BUSINESS", 2500);
        prices.put("FIRST", 3500);

        Map<String, Integer> seats = new HashMap<>();
        seats.put("ECONOMY", 150);
        seats.put("BUSINESS", 30);
        seats.put("FIRST", 20);

        TrainResponse train = TrainResponse.builder()
                .id(1L)
                .name("Intercity Express")
                .type("EXPRESS")
                .route("Colombo Fort - Kandy")
                .fromStation("Colombo Fort")
                .toStation("Kandy")
                .departureTime(LocalTime.of(8, 30))
                .arrivalTime(LocalTime.of(11, 45))
                .duration("3h 15m")
                .distance("120 km")
                .price(prices)
                .seats(seats)
                .status("ACTIVE")
                .features(Arrays.asList("WiFi", "AC", "Food Service"))
                .build();

        when(trainService.getTrainById(1L)).thenReturn(train);

        mockMvc.perform(get("/api/trains/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.name").value("Intercity Express"))
                .andExpect(jsonPath("$.data.id").value(1));
    }

    @Test
    void searchTrains_ShouldReturnSuccess_WhenValidSearchRequest() throws Exception {
        TrainSearchRequest request = new TrainSearchRequest();
        request.setFrom("Colombo Fort");
        request.setTo("Kandy");
        request.setDate(LocalDate.of(2024, 12, 15));
        request.setAdults(2);
        request.setChildren(1);

        Map<String, Integer> prices = new HashMap<>();
        prices.put("ECONOMY", 1200);
        prices.put("BUSINESS", 2500);
        prices.put("FIRST", 3500);

        Map<String, Integer> seats = new HashMap<>();
        seats.put("ECONOMY", 150);
        seats.put("BUSINESS", 30);
        seats.put("FIRST", 20);

        TrainResponse train = TrainResponse.builder()
                .id(1L)
                .name("Intercity Express")
                .type("EXPRESS")
                .route("Colombo Fort - Kandy")
                .fromStation("Colombo Fort")
                .toStation("Kandy")
                .departureTime(LocalTime.of(8, 30))
                .arrivalTime(LocalTime.of(11, 45))
                .duration("3h 15m")
                .distance("120 km")
                .price(prices)
                .seats(seats)
                .status("ACTIVE")
                .features(Arrays.asList("WiFi", "AC", "Food Service"))
                .build();

        when(trainService.searchTrains(any(TrainSearchRequest.class))).thenReturn(Arrays.asList(train));

        mockMvc.perform(post("/api/trains/search")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data[0].name").value("Intercity Express"))
                .andExpect(jsonPath("$.data[0].fromStation").value("Colombo Fort"))
                .andExpect(jsonPath("$.data[0].toStation").value("Kandy"));
    }

    @Test
    void searchTrains_ShouldReturnValidationError_WhenInvalidRequest() throws Exception {
        TrainSearchRequest request = new TrainSearchRequest();
        request.setFrom("");
        request.setTo("Kandy");
        request.setDate(LocalDate.of(2024, 12, 15));
        request.setAdults(2);
        request.setChildren(1);

        mockMvc.perform(post("/api/trains/search")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }
}
