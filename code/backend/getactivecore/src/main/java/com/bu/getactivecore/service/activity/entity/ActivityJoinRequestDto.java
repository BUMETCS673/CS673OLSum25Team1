package com.bu.getactivecore.service.activity.entity;

import jakarta.validation.constraints.NotBlank;
import lombok.Value;

/**
 * The request DTO for joining an activity.
 * <p>
 * The id of user that is requesting to join will be extracted from the JWT token.
 */
@Value
public class ActivityJoinRequestDto {

    /**
     * The ID of the activity to join.
     */
    @NotBlank(message = "Activity ID must be provided")
    String activityId;
}
