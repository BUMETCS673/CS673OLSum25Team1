package com.bu.getactivecore.service.activity;

import com.bu.getactivecore.model.activity.Activity;
import com.bu.getactivecore.model.activity.RoleType;
import com.bu.getactivecore.model.activity.UserActivity;
import com.bu.getactivecore.repository.ActivityRepository;
import com.bu.getactivecore.repository.UserActivityRepository;
import com.bu.getactivecore.service.activity.api.ActivityApi;
import com.bu.getactivecore.service.activity.entity.ActivityCreateRequestDto;
import com.bu.getactivecore.service.activity.entity.ActivityDto;
import com.bu.getactivecore.service.activity.entity.UserActivityDto;
import com.bu.getactivecore.shared.ApiErrorPayload;
import com.bu.getactivecore.shared.exception.ApiException;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
/**
 * Core logic for managing activities.
 */
@Service
public class ActivityService implements ActivityApi {

    private final UserActivityRepository m_userActivityRepo;

    private final ActivityRepository m_activityRepo;

    /**
     * Constructs the ActivityService.
     *
     * @param activityRepo used to fetch and manage activities
     * @param userActivityRepo
     * @param participantActivityRepo
     */
    public ActivityService(ActivityRepository activityRepo, UserActivityRepository userActivityRepo) {
        m_activityRepo = activityRepo;
        m_userActivityRepo = userActivityRepo;
    }

    @Override
    public List<ActivityDto> getActivityByName(String activityName) {
        List<Activity> activities = m_activityRepo.findByNameContaining(activityName);
        return activities.stream().map(ActivityDto::of).toList();
    }


    @Override
    public List<ActivityDto> getAllActivities() {
        List<Activity> activities = m_activityRepo.findAll();
        return activities.stream().map(ActivityDto::of).toList();
    }

    @Override
    @Transactional
    public void createActivity(String userId, ActivityCreateRequestDto requestDto) {
        m_activityRepo.findByName(requestDto.getName()).ifPresent(a -> {
            throw new ApiException(ApiErrorPayload.builder().status(HttpStatus.BAD_REQUEST).message("Activity name exists").build());
        });

        if (requestDto.getEndDateTime().isEqual(requestDto.getStartDateTime())
                || requestDto.getEndDateTime().isBefore(requestDto.getStartDateTime())) {
            throw new ApiException(ApiErrorPayload.builder().status(HttpStatus.BAD_REQUEST)
                    .message("End date time cannot be on or before start date time")
                    .build()
            );
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
    public List<UserActivityDto> getParticipantActivities(String userId) {
        List<UserActivityDto> userActivityDtos = m_userActivityRepo.findByUserId(userId).stream().map(UserActivityDto::of).toList();
        return userActivityDtos;
    }

    @Override
    public void joinActivity(String userId, String activityId) {
        m_userActivityRepo.findByUserIdAndActivityId(userId, activityId).ifPresent(userActivity -> {
            throw new ApiException(ApiErrorPayload.builder().status(HttpStatus.BAD_REQUEST).message("User already joined activity").build());
        });
        Activity activity = m_activityRepo.findById(activityId).orElseThrow(() -> new ApiException(ApiErrorPayload.builder().status(HttpStatus.BAD_REQUEST).message("Activity not found").build()));
        m_userActivityRepo.save(UserActivity.builder()
                .userId(userId)
                .activity(activity)
                .role(RoleType.PARTICIPANT)
                .build());
    }

    @Override
    public void leaveActivity(String userId, String activityId) {
        m_userActivityRepo.findByUserIdAndActivityId(userId, activityId).ifPresent(userActivity -> {
            m_userActivityRepo.delete(userActivity);
        });
    }
}
