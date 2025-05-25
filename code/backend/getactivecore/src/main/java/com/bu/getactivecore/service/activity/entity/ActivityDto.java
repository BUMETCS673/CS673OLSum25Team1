package com.bu.getactivecore.service.activity.entity;

import com.bu.getactivecore.model.activity.Activity;
import lombok.Builder;
import lombok.Data;

/**
 * Activity DTO for exposing activity data.
 */
@Data
@Builder
public class ActivityDto {

    /**
     * Unique identifier for the activity.
     */
    private String activityId;

    /**
     * Name of the activity.
     */
    private String name;

    /**
     * Start time of the activity in milliseconds since epoch.
     */
    private Long startTimeMs;

    /**
     * Converts an Activity entity to an ActivityDto.
     *
     * @param activity the Activity entity
     * @return the ActivityDto
     */
    public static ActivityDto of(Activity activity) {
        return ActivityDto.builder()
                .activityId(activity.getId())
                .name(activity.getName())
                .build();
    }

    public static Activity from(ActivityCreateRequestDto request) {
        return Activity.builder()
                .name(request.getName())
                .build();
    }
}
