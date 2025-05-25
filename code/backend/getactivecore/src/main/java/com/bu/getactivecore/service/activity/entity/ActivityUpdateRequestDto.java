package com.bu.getactivecore.service.activity.entity;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.Value;

/**
 * DTO for updating an existing activity.
 */
@Value
public class ActivityUpdateRequestDto {

    /**
     * The ID of the activity to update.
     */
    @NotBlank(message = "Activity ID must be provided")
    String activityId;

    /**
     * The updated name of the activity.
     * <p> The validation will run if the field is set, otherwise it will be ignored.
     */
    @Size(min = 2, message = "Activity name must not be empty")
    @Size(max = 100, message = "Activity name must not exceed 100 characters")
    String updatedActivityName;

    /**
     * The new description of the activity.
     * <p> The validation will run if the field is set,otherwise it will be ignored.
     */
    @Size(min = 1, message = "Activity description must not be empty")
    @Size(max = 500, message = "Activity description must not exceed 500 characters")
    String updatedActivityDescription;

    /**
     * The updated location of the activity.
     * <p> The validation will run if the field is set, otherwise it will be ignored.
     */
    @Size(min = 1, message = "Activity location must not be empty")
    @Size(max = 100, message = "Activity location must not exceed 100 characters")
    String updatedActivityLocation;

    @Positive(message = "Start time must be a positive number")
    Long updatedStartTime;

}
