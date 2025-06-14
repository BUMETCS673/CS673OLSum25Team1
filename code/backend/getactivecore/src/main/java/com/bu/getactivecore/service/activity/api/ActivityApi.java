package com.bu.getactivecore.service.activity.api;

import com.bu.getactivecore.service.activity.entity.ActivityCommentCreateRequestDto;
import com.bu.getactivecore.service.activity.entity.ActivityCommentDto;
import com.bu.getactivecore.service.activity.entity.ActivityCreateRequestDto;
import com.bu.getactivecore.service.activity.entity.ActivityDeleteRequestDto;
import com.bu.getactivecore.service.activity.entity.ActivityDto;
import com.bu.getactivecore.service.activity.entity.ActivityUpdateRequestDto;
import com.bu.getactivecore.service.activity.entity.UserActivityDto;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Interface for managing activities.
 */
public interface ActivityApi {

    /**
     * Retrieves all activities.
     *
     * @return List of all activities
     */
    Page<ActivityDto> getAllActivities(Pageable page);

     /**
     * Retrieves all activities sorted by popularity.
     *
     * @return List of all activities
     */
    Page<ActivityDto> getAllActivitiesSortedByPopularity(Pageable page);

    /**
     * Retrieves activities by their name.
     *
     * @param activityName Name of the activity to search for
     * @return List of activities matching the given name
     */
    Page<ActivityDto> getActivityByName(String activityName, Pageable page);

    /**
     * Creates a new activity.
     *
     * @param userId     ID of the user creating the activity
     * @param requestDto Details of the activity to be created
     * @return Response containing details of the created activity
     */
    void createActivity(String userId, @Valid ActivityCreateRequestDto requestDto);

    /**
     * Delete an activity.
     *
     * @param activityId ID of a to be deleted activity
     * @param requestDto Details of the activity to be deleted
     */
    void deleteActivity(String activityId, @Valid ActivityDeleteRequestDto requestDto);

    /**
     * Update an activity.
     *
     * @param activityId ID of a to be deleted activity
     * @param requestDto Details of the activity to be updated
     * @return Response containing details of the updated activity
     */
    ActivityDto updateActivity(String activityId, @Valid ActivityUpdateRequestDto requestDto);

    /**
     * Retrieves a list of joined activities for the requested user.
     *
     * @param userId ID of the user whose joined activities are to be fetched
     * @return List of {@link UserActivityDto} representing the activities the user has joined
     */
    List<UserActivityDto> getJoinedActivities(String userId);

    /**
     * Joins an activity.
     *
     * @param userId     ID of the user joining the activity
     * @param activityId ID of the activity to join
     */
    void joinActivity(String userId, String activityId);

    /**
     * Leaves an activity.
     *
     * @param userId     ID of the user leaving the activity
     * @param activityId ID of the activity to leave
     */
    void leaveActivity(String userId, String activityId);

    /**
     * Creates a new activity comment.
     *
     * @param userId     ID of the user creating the activity comment
     * @param activityId  ID of the activity'
     * @param requestDto Details of the activity comment
     * @param timestamp  comment creation timestamp
     */
    void createActivityComment(String userId, String activityId, @Valid ActivityCommentCreateRequestDto requestDto, LocalDateTime timestamp);

    /**
     * Retrieves all activity comments.
     *
     * @return List of all activity comments
     */
    Page<ActivityCommentDto> getAllActivityComments(Pageable page, String activityId);
}
