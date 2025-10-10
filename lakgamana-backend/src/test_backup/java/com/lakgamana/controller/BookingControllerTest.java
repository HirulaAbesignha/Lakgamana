package com.lakgamana.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.lakgamana.dto.request.BookingRequest;
import com.lakgamana.dto.response.BookingResponse;
import com.lakgamana.service.BookingService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Arrays;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(BookingController.class)
class BookingControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private BookingService bookingService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @WithMockUser(roles = "ADMIN")
    void getAllBookings_ShouldReturnSuccess_WhenBookingsExist() throws Exception {
        BookingResponse.PassengerResponse passenger = BookingResponse.PassengerResponse.builder()
                .name("John Doe")
                .age(35)
                .gender("MALE")
                .idType("PASSPORT")
                .idNumber("P1234567")
                .build();

        BookingResponse booking = BookingResponse.builder()
                .id(1L)
                .userId(1L)
                .userName("John Doe")
                .trainId(1L)
                .trainName("Intercity Express")
                .route("Colombo Fort - Kandy")
                .departureDate(LocalDate.of(2024, 12, 15))
                .departureTime(LocalTime.of(8, 30))
                .arrivalTime(LocalTime.of(11, 45))
                .seatClass("ECONOMY")
                .seatNumber("E-15")
                .passengers(Arrays.asList(passenger))
                .totalAmount(new BigDecimal("1200.00"))
                .status("CONFIRMED")
                .bookingDate(LocalDateTime.of(2024, 12, 10, 10, 30))
                .paymentMethod("CREDIT_CARD")
                .paymentStatus("COMPLETED")
                .build();

        when(bookingService.getAllBookings()).thenReturn(Arrays.asList(booking));

        mockMvc.perform(get("/api/bookings"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data[0].trainName").value("Intercity Express"))
                .andExpect(jsonPath("$.data[0].status").value("CONFIRMED"));
    }

    @Test
    @WithMockUser(roles = "USER")
    void getUserBookings_ShouldReturnSuccess_WhenUserHasBookings() throws Exception {
        BookingResponse.PassengerResponse passenger = BookingResponse.PassengerResponse.builder()
                .name("John Doe")
                .age(35)
                .gender("MALE")
                .idType("PASSPORT")
                .idNumber("P1234567")
                .build();

        BookingResponse booking = BookingResponse.builder()
                .id(1L)
                .userId(1L)
                .userName("John Doe")
                .trainId(1L)
                .trainName("Intercity Express")
                .route("Colombo Fort - Kandy")
                .departureDate(LocalDate.of(2024, 12, 15))
                .departureTime(LocalTime.of(8, 30))
                .arrivalTime(LocalTime.of(11, 45))
                .seatClass("ECONOMY")
                .seatNumber("E-15")
                .passengers(Arrays.asList(passenger))
                .totalAmount(new BigDecimal("1200.00"))
                .status("CONFIRMED")
                .bookingDate(LocalDateTime.of(2024, 12, 10, 10, 30))
                .paymentMethod("CREDIT_CARD")
                .paymentStatus("COMPLETED")
                .build();

        when(bookingService.getUserBookings(1L)).thenReturn(Arrays.asList(booking));

        mockMvc.perform(get("/api/bookings/user/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data[0].userId").value(1))
                .andExpect(jsonPath("$.data[0].trainName").value("Intercity Express"));
    }

    @Test
    @WithMockUser(roles = "USER")
    void createBooking_ShouldReturnSuccess_WhenValidRequest() throws Exception {
        BookingRequest.PassengerRequest passengerRequest = new BookingRequest.PassengerRequest();
        passengerRequest.setName("John Doe");
        passengerRequest.setAge(35);
        passengerRequest.setGender("MALE");
        passengerRequest.setIdType("PASSPORT");
        passengerRequest.setIdNumber("P1234567");

        BookingRequest request = new BookingRequest();
        request.setTrainId(1L);
        request.setDepartureDate(LocalDate.of(2024, 12, 15));
        request.setSeatClass("ECONOMY");
        request.setPassengers(Arrays.asList(passengerRequest));

        BookingResponse.PassengerResponse passenger = BookingResponse.PassengerResponse.builder()
                .name("John Doe")
                .age(35)
                .gender("MALE")
                .idType("PASSPORT")
                .idNumber("P1234567")
                .build();

        BookingResponse response = BookingResponse.builder()
                .id(1L)
                .userId(1L)
                .userName("John Doe")
                .trainId(1L)
                .trainName("Intercity Express")
                .route("Colombo Fort - Kandy")
                .departureDate(LocalDate.of(2024, 12, 15))
                .departureTime(LocalTime.of(8, 30))
                .arrivalTime(LocalTime.of(11, 45))
                .seatClass("ECONOMY")
                .seatNumber("E-15")
                .passengers(Arrays.asList(passenger))
                .totalAmount(new BigDecimal("1200.00"))
                .status("PENDING")
                .bookingDate(LocalDateTime.now())
                .paymentMethod("CREDIT_CARD")
                .paymentStatus("PENDING")
                .build();

        when(bookingService.createBooking(any(BookingRequest.class), anyLong())).thenReturn(response);

        mockMvc.perform(post("/api/bookings")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.trainName").value("Intercity Express"))
                .andExpect(jsonPath("$.data.status").value("PENDING"));
    }

    @Test
    @WithMockUser(roles = "USER")
    void createBooking_ShouldReturnValidationError_WhenInvalidRequest() throws Exception {
        BookingRequest request = new BookingRequest();
        request.setTrainId(null);
        request.setDepartureDate(LocalDate.of(2024, 12, 15));
        request.setSeatClass("ECONOMY");

        mockMvc.perform(post("/api/bookings")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }
}
