package com.lakgamana.controller;

import com.lakgamana.dto.request.TrainSearchRequest;
import com.lakgamana.dto.response.ApiResponse;
import com.lakgamana.dto.response.TrainResponse;
import com.lakgamana.entity.Train;
import com.lakgamana.entity.enums.TrainType;
import com.lakgamana.entity.enums.TrainStatus;
import com.lakgamana.service.TrainService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
 
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/trains")
@Tag(name = "Trains", description = "Train management APIs")
public class TrainController {

    private final TrainService trainService;

    private static final org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger(TrainController.class);

    public TrainController(TrainService trainService) {
        this.trainService = trainService;
    }

    @GetMapping("/search")
    @Operation(summary = "Search trains", description = "Search available trains based on criteria")
    public ResponseEntity<ApiResponse<List<TrainResponse>>> searchTrains(@Valid TrainSearchRequest searchRequest) {
        try {
            List<Train> trains = trainService.findAvailableTrains(searchRequest);
            List<TrainResponse> trainResponses = trains.stream()
                    .map(TrainResponse::fromEntity)
                    .toList();
            return ResponseEntity.ok(ApiResponse.success("Trains found", trainResponses));
        } catch (Exception e) {
            log.error("Train search failed", e);
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error("Train search failed: " + e.getMessage()));
        }
    }

    @GetMapping("/available")
    @Operation(summary = "Get available trains", description = "Get all active trains")
    public ResponseEntity<ApiResponse<List<TrainResponse>>> getAvailableTrains() {
        try {
            List<Train> trains = trainService.findByStatus(TrainStatus.ACTIVE);
            List<TrainResponse> trainResponses = trains.stream()
                    .map(TrainResponse::fromEntity)
                    .toList();
            return ResponseEntity.ok(ApiResponse.success("Available trains retrieved", trainResponses));
        } catch (Exception e) {
            log.error("Failed to get available trains", e);
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error("Failed to get available trains: " + e.getMessage()));
        }
    }

    @GetMapping("/stations")
    @Operation(summary = "Get all stations", description = "Get list of all available stations")
    public ResponseEntity<ApiResponse<StationResponse>> getAllStations() {
        try {
            List<String> fromStations = trainService.getAllFromStations();
            List<String> toStations = trainService.getAllToStations();
            StationResponse response = new StationResponse(fromStations, toStations);
            return ResponseEntity.ok(ApiResponse.success("Stations retrieved", response));
        } catch (Exception e) {
            log.error("Failed to get stations", e);
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error("Failed to get stations: " + e.getMessage()));
        }
    }

    @GetMapping
    @Operation(summary = "Get all trains (Admin)", description = "Get all trains with filtering and pagination")
    public ResponseEntity<ApiResponse<Page<TrainResponse>>> getAllTrains(
            @RequestParam(required = false) String fromStation,
            @RequestParam(required = false) String toStation,
            @RequestParam(required = false) TrainType type,
            @RequestParam(required = false) TrainStatus status,
            @PageableDefault(size = 20, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable) {
        try {
            Page<Train> trains = trainService.findTrainsWithFilters(fromStation, toStation, type, status, pageable);
            Page<TrainResponse> trainResponses = trains.map(TrainResponse::fromEntity);
            return ResponseEntity.ok(ApiResponse.success("Trains retrieved", trainResponses));
        } catch (Exception e) {
            log.error("Failed to get trains", e);
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error("Failed to get trains: " + e.getMessage()));
        }
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get train by ID", description = "Get train details by ID")
    public ResponseEntity<ApiResponse<TrainResponse>> getTrainById(@PathVariable Long id) {
        try {
            Train train = trainService.findById(id);
            TrainResponse trainResponse = TrainResponse.fromEntity(train);
            return ResponseEntity.ok(ApiResponse.success("Train retrieved", trainResponse));
        } catch (Exception e) {
            log.error("Failed to get train with id: {}", id, e);
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error("Failed to get train: " + e.getMessage()));
        }
    }

    @PostMapping
    @Operation(summary = "Create train (Admin)", description = "Create a new train")
    public ResponseEntity<ApiResponse<TrainResponse>> createTrain(@Valid @RequestBody Train train) {
        try {
            Train createdTrain = trainService.createTrain(train);
            TrainResponse trainResponse = TrainResponse.fromEntity(createdTrain);
            return ResponseEntity.ok(ApiResponse.success("Train created successfully", trainResponse));
        } catch (Exception e) {
            log.error("Failed to create train", e);
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error("Failed to create train: " + e.getMessage()));
        }
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update train (Admin)", description = "Update train details")
    public ResponseEntity<ApiResponse<TrainResponse>> updateTrain(@PathVariable Long id, @Valid @RequestBody Train train) {
        try {
            Train updatedTrain = trainService.updateTrain(id, train);
            TrainResponse trainResponse = TrainResponse.fromEntity(updatedTrain);
            return ResponseEntity.ok(ApiResponse.success("Train updated successfully", trainResponse));
        } catch (Exception e) {
            log.error("Failed to update train with id: {}", id, e);
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error("Failed to update train: " + e.getMessage()));
        }
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete train (Admin)", description = "Delete a train")
    public ResponseEntity<ApiResponse<Void>> deleteTrain(@PathVariable Long id) {
        try {
            trainService.deleteTrain(id);
            return ResponseEntity.ok(ApiResponse.success("Train deleted successfully", null));
        } catch (Exception e) {
            log.error("Failed to delete train with id: {}", id, e);
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error("Failed to delete train: " + e.getMessage()));
        }
    }

    @GetMapping("/stats")
    @Operation(summary = "Get train statistics (Admin)", description = "Get train statistics")
    public ResponseEntity<ApiResponse<TrainStatsResponse>> getTrainStats() {
        try {
            long totalTrains = trainService.countActiveTrains();
            List<Train> recentTrains = trainService.findRecentActiveTrains(org.springframework.data.domain.PageRequest.of(0, 5));
            
            TrainStatsResponse stats = new TrainStatsResponse();
            stats.setTotalTrains(totalTrains);
            stats.setRecentTrains(recentTrains.stream().map(TrainResponse::fromEntity).toList());
            
            return ResponseEntity.ok(ApiResponse.success("Train statistics retrieved", stats));
        } catch (Exception e) {
            log.error("Failed to get train statistics", e);
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error("Failed to get train statistics: " + e.getMessage()));
        }
    }

    // Helper classes
    public static class StationResponse {
        public final List<String> fromStations;
        public final List<String> toStations;

        public StationResponse(List<String> fromStations, List<String> toStations) {
            this.fromStations = fromStations;
            this.toStations = toStations;
        }
    }

    public static class TrainStatsResponse {
        private long totalTrains;
        private List<TrainResponse> recentTrains;
        public TrainStatsResponse() {}
        public long getTotalTrains() { return totalTrains; }
        public void setTotalTrains(long totalTrains) { this.totalTrains = totalTrains; }
        public List<TrainResponse> getRecentTrains() { return recentTrains; }
        public void setRecentTrains(List<TrainResponse> recentTrains) { this.recentTrains = recentTrains; }
    }
}
