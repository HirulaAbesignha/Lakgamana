package com.lakgamana.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TrainSearchRequest {

    @NotBlank(message = "From station is required")
    private String from;

    @NotBlank(message = "To station is required")
    private String to;

    @NotNull(message = "Departure date is required")
    private LocalDate date;

    @Positive(message = "Number of adults must be positive")
    @Builder.Default
    private Integer adults = 1;

    @Builder.Default
    private Integer children = 0;
}
