package com.bu.getactivecore.service.activity.entity;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.Value;

/**
 * The DTO for creating a new activity.
 */
@Value
public class ActivityCreateRequestDto {

    @NotBlank(message = "Activity name must be provided")
    @Size(min = 2, message = "Activity name must be at least 2 characters long")
    @Size(max = 100, message = "Activity name must not exceed 100 characters")
    String name;

    /**
     * The start time of the activity in milliseconds since epoch.
     */
    @NotNull(message = "Start time must be provided")
    @Positive(message = "Start time must be a positive number")
    Long startTimeMs;
}
