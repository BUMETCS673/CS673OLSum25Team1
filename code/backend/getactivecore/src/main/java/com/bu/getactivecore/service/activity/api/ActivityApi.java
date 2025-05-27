package com.bu.getactivecore.service.activity.api;

import com.bu.getactivecore.model.activity.Activity;
import com.bu.getactivecore.service.activity.entity.ActivityCreateRequestDto;
import com.bu.getactivecore.service.activity.entity.ActivityDto;
import com.bu.getactivecore.service.activity.entity.ActivityResponseDto;

import java.util.List;

/**
 * Interface for managing activities.
 */
public interface ActivityApi {
    List<ActivityDto> getAllActivities();

    List<ActivityDto> getActivityByName(String activityName);

    /**
     * Creates a new activity.
     *
     * @param userId  ID of the user creating the activity
     * @param request Details of the activity to create
     * @return Response containing details of the created activity
     */
    void createActivity(String userId, Activity activity);
}
