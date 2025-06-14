package com.bu.getactivecore.service.activity;

import com.bu.getactivecore.model.activity.Activity;
import com.bu.getactivecore.model.activity.ActivityComment;
import com.bu.getactivecore.model.activity.RoleType;
import com.bu.getactivecore.model.activity.UserActivity;
import com.bu.getactivecore.repository.ActivityCommentRepository;
import com.bu.getactivecore.repository.ActivityRepository;
import com.bu.getactivecore.repository.UserActivityRepository;
import com.bu.getactivecore.service.activity.api.ActivityApi;
import com.bu.getactivecore.service.activity.entity.ActivityCommentCreateRequestDto;
import com.bu.getactivecore.service.activity.entity.ActivityCommentDto;
import com.bu.getactivecore.service.activity.entity.ActivityCreateRequestDto;
import com.bu.getactivecore.service.activity.entity.ActivityDeleteRequestDto;
import com.bu.getactivecore.service.activity.entity.ActivityDto;
import com.bu.getactivecore.service.activity.entity.ActivityUpdateRequestDto;
import com.bu.getactivecore.service.activity.entity.UserActivityDto;
import com.bu.getactivecore.shared.ApiErrorPayload;
import com.bu.getactivecore.shared.exception.ApiException;

import jakarta.validation.Valid;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * Core logic for managing activities.
 */
@Service
public class ActivityService implements ActivityApi {

    private final UserActivityRepository m_userActivityRepo;

    private final ActivityRepository m_activityRepo;

    private final ActivityCommentRepository m_activityCommentRepo;

    /**
     * Constructs the ActivityService.
     *
     * @param activityRepo     used to fetch and manage activities
     * @param userActivityRepo used to fetch and manage user activities
     * @param activityCommentRepo used to fetch and manage user activities
     */
    public ActivityService(ActivityRepository activityRepo, UserActivityRepository userActivityRepo,
    ActivityCommentRepository activityCommentRepo) {
        m_activityRepo = activityRepo;
        m_userActivityRepo = userActivityRepo;
        m_activityCommentRepo = activityCommentRepo;
    }

    @Override
    public Page<ActivityDto> getActivityByName(String activityName, Pageable pageable) {
        Page<Activity> activities = m_activityRepo.findByNameContaining(activityName, pageable);
        return activities.map(ActivityDto::of);
    }

    @Override
    public Page<ActivityDto> getAllActivitiesSortedByPopularity(Pageable pageable) {
        Page<Activity> activities = m_activityRepo.findAllSortedByPopularity(pageable);
        return activities.map(ActivityDto::of);
    }

    @Override
    public Page<ActivityDto> getAllActivities(Pageable pageable) {
        Page<Activity> activities = m_activityRepo.findAll(pageable);
        return activities.map(ActivityDto::of);
    }

    @Override
    @Transactional
    public void createActivity(String userId, ActivityCreateRequestDto requestDto) {
        m_activityRepo.findByName(requestDto.getName()).ifPresent(a -> {
            throw new ApiException(
                    ApiErrorPayload.builder().status(HttpStatus.BAD_REQUEST).message("Activity name exists").build());
        });

        if (requestDto.getEndDateTime().isEqual(requestDto.getStartDateTime())
                || requestDto.getEndDateTime().isBefore(requestDto.getStartDateTime())) {
            throw new ApiException(ApiErrorPayload.builder().status(HttpStatus.BAD_REQUEST)
                    .message("End date time cannot be on or before start date time")
                    .build());
        }

        if (requestDto.getStartDateTime().isBefore(LocalDateTime.now())
                || requestDto.getEndDateTime().isBefore(requestDto.getStartDateTime())) {
            throw new ApiException(ApiErrorPayload.builder().status(HttpStatus.BAD_REQUEST)
                    .message("Start date time cannot be in the past")
                    .build());
        }

        if (requestDto.getEndDateTime().isBefore(LocalDateTime.now())
                || requestDto.getEndDateTime().isBefore(requestDto.getStartDateTime())) {
            throw new ApiException(ApiErrorPayload.builder().status(HttpStatus.BAD_REQUEST)
                    .message("End date time cannot be in the past")
                    .build());
        }

        Activity createdActivity = m_activityRepo.save(ActivityCreateRequestDto.from(requestDto));
        UserActivity userActivityRole = UserActivity.builder()
                .userId(userId)
                .activity(createdActivity)
                .role(RoleType.ADMIN)
                .build();
        m_userActivityRepo.save(userActivityRole);
    }

    @Override
    public void deleteActivity(String activityId, ActivityDeleteRequestDto requestDto) {
        Optional<Activity> activity = m_activityRepo.findById(activityId);

        if (activity.isEmpty()) {
            throw new ApiException(
                    ApiErrorPayload.builder().status(HttpStatus.BAD_REQUEST).message("Activity not found").build());
        }

        List<UserActivity> userActivities = m_userActivityRepo.findByActivityIdAndRole(activityId,
                RoleType.PARTICIPANT);
        if (!requestDto.isForce() && !userActivities.isEmpty()) {
            throw new ApiException(ApiErrorPayload.builder().status(HttpStatus.FORBIDDEN)
                    .message("Force is set to false. There are participants in this activity. ").build());
        }

        m_userActivityRepo.deleteByActivityId(activityId);
        m_activityRepo.deleteById(activityId);
    }

    @Override
    public ActivityDto updateActivity(String id, ActivityUpdateRequestDto requestDto) {
        Optional<Activity> activity = m_activityRepo.findById(id);

        if (activity.isEmpty()) {
            throw new ApiException(
                    ApiErrorPayload.builder().status(HttpStatus.BAD_REQUEST).message("Activity not found").build());
        }

        if (requestDto.getEndDateTime().isBefore(LocalDateTime.now())
                || requestDto.getEndDateTime().isBefore(requestDto.getStartDateTime())) {
            throw new ApiException(ApiErrorPayload.builder().status(HttpStatus.BAD_REQUEST)
                    .message("End date time cannot be in the past")
                    .build());
        }

        if (requestDto.getEndDateTime().isEqual(requestDto.getStartDateTime())
                || requestDto.getEndDateTime().isBefore(requestDto.getStartDateTime())) {
            throw new ApiException(ApiErrorPayload.builder().status(HttpStatus.BAD_REQUEST)
                    .message("End date time cannot be on or before start date time")
                    .build());
        }

        if (requestDto.getStartDateTime().isBefore(LocalDateTime.now())
                || requestDto.getEndDateTime().isBefore(requestDto.getStartDateTime())) {
            throw new ApiException(ApiErrorPayload.builder().status(HttpStatus.BAD_REQUEST)
                    .message("Start date time cannot be in the past")
                    .build());
        }

        Activity updateActivity = m_activityRepo.save(ActivityUpdateRequestDto.from(id, requestDto));
        return ActivityDto.of(updateActivity);
    }

    @Override
    public List<UserActivityDto> getJoinedActivities(String userId) {
        return m_userActivityRepo.findJoinedActivitiesByUserId(userId).stream()
                .map(UserActivityDto::of).toList();
    }

    @Override
    public void joinActivity(String userId, String activityId) {
        m_userActivityRepo.findByUserIdAndActivityId(userId, activityId).ifPresent(userActivity -> {
            throw new ApiException(ApiErrorPayload.builder().status(HttpStatus.BAD_REQUEST)
                    .message("User already joined activity").build());
        });
        Activity activity = m_activityRepo.findById(activityId).orElseThrow(() -> new ApiException(
                ApiErrorPayload.builder().status(HttpStatus.BAD_REQUEST).message("Activity not found").build()));
        m_userActivityRepo.save(UserActivity.builder()
                .userId(userId)
                .activity(activity)
                .role(RoleType.PARTICIPANT)
                .build());
    }

    @Override
    public void leaveActivity(String userId, String activityId) {
        m_userActivityRepo.findByUserIdAndActivityId(userId, activityId).ifPresent(m_userActivityRepo::delete);
    }

    @Override
    public void createActivityComment(String userId, String activityId, @Valid ActivityCommentCreateRequestDto requestDto, LocalDateTime timestamp) {
        Optional<Activity> activity = m_activityRepo.findById(activityId);
        if (activity.isEmpty()) {
            throw new ApiException(
                    ApiErrorPayload.builder().status(HttpStatus.BAD_REQUEST).message("Activity not found").build());
        }         

       ActivityComment activityComment = ActivityComment.builder().activityId(activityId)
                                            .userId(userId)
                                            .comment(requestDto.getComment())
                                            .timestamp(timestamp)
                                            .build();         
       m_activityCommentRepo.save(activityComment);
    
    }

    @Override
    public Page<ActivityCommentDto> getAllActivityComments(Pageable page, String activityId) {
        Optional<Activity> activity = m_activityRepo.findById(activityId);

        if (activity.isEmpty()) {
            throw new ApiException(
                    ApiErrorPayload.builder().status(HttpStatus.BAD_REQUEST).message("Activity not found").build());
        }

        Page<ActivityComment> comments = m_activityCommentRepo.findAllByActivityId(page, activityId);
        return comments.map(ActivityCommentDto::of);
    }
}
