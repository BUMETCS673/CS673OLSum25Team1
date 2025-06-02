package com.bu.getactivecore.service.activity;

import com.bu.getactivecore.model.activity.Activity;
import com.bu.getactivecore.model.activity.RoleType;
import com.bu.getactivecore.model.activity.UserActivity;
import com.bu.getactivecore.model.users.Users;
import com.bu.getactivecore.repository.ActivityRepository;
import com.bu.getactivecore.repository.UserActivityRepository;
import com.bu.getactivecore.repository.UserRepository;
import com.bu.getactivecore.service.activity.api.ActivityApi;
import com.bu.getactivecore.service.activity.entity.ActivityCreateRequestDto;
import com.bu.getactivecore.service.activity.entity.ActivityDeleteRequestDto;
import com.bu.getactivecore.service.activity.entity.ActivityDto;
import com.bu.getactivecore.service.activity.entity.ActivityUpdateRequestDto;
import com.bu.getactivecore.shared.ApiErrorPayload;
import com.bu.getactivecore.shared.exception.ApiException;

import jakarta.validation.Valid;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
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

    /**
     * Constructs the ActivityService.
     *
     * @param activityRepo used to fetch and manage activities
     */
    public ActivityService(ActivityRepository activityRepo, UserActivityRepository userActivityRepo) {
        m_activityRepo = activityRepo;
        m_userActivityRepo = userActivityRepo;
    }

    @Override
    public List<ActivityDto> getActivityByName(String activityName, int page, int size) {
        PageRequest pageable = PageRequest.of(page, size);
        Page<Activity> activities = m_activityRepo.findByNameContaining(activityName, pageable);
        return activities.stream().map(ActivityDto::of).toList();
    }


    @Override
    public List<ActivityDto> getAllActivities(int page, int size) {
        PageRequest pageable = PageRequest.of(page, size);
        Page<Activity> activities = m_activityRepo.findAll(pageable);
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

        if (requestDto.getStartDateTime().isBefore(LocalDateTime.now())
                || requestDto.getEndDateTime().isBefore(requestDto.getStartDateTime())) {
            throw new ApiException(ApiErrorPayload.builder().status(HttpStatus.BAD_REQUEST)
                    .message("Start date time cannot be in the past")
                    .build()
            );
        }

        if (requestDto.getEndDateTime().isBefore(LocalDateTime.now())
                || requestDto.getEndDateTime().isBefore(requestDto.getStartDateTime())) {
            throw new ApiException(ApiErrorPayload.builder().status(HttpStatus.BAD_REQUEST)
                    .message("End date time cannot be in the past")
                    .build()
            );
        }

        Activity createdActivity = m_activityRepo.save(ActivityCreateRequestDto.from(requestDto));
        UserActivity userActivityRole = UserActivity.builder()
                .userId(userId)
                .activityId(createdActivity.getId())
                .role(RoleType.ADMIN)
                .build();        
        m_userActivityRepo.save(userActivityRole);
    }

    @Override
    public void deleteActivity(String userId, String activityId, ActivityDeleteRequestDto requestDto) {
        Optional<Activity> activity = m_activityRepo.findById(activityId);

        if(activity.isEmpty()){
              throw new ApiException(ApiErrorPayload.builder().status(HttpStatus.BAD_REQUEST).message("Activity not found").build());
        }

        Optional<List<UserActivity>> userActivities= m_userActivityRepo.findByActivityIdAndRole(activityId, RoleType.PARTICIPANT);
        if(requestDto.isForce() == false && !userActivities.isEmpty() && !userActivities.get().isEmpty()) {
            throw new ApiException(ApiErrorPayload.builder().status(HttpStatus.FORBIDDEN).message("Force is set to false. There are participants in this activity. ").build());
        }

        m_userActivityRepo.deleteByActivityId(activityId);
        m_activityRepo.deleteById(activityId);
    }

    @Override
    public ActivityDto updateActivity(String userId, String id, ActivityUpdateRequestDto requestDto) {
        if (requestDto.getEndDateTime().isEqual(requestDto.getStartDateTime())
                || requestDto.getEndDateTime().isBefore(requestDto.getStartDateTime())) {
            throw new ApiException(ApiErrorPayload.builder().status(HttpStatus.BAD_REQUEST)
                    .message("End date time cannot be on or before start date time")
                    .build()
            );
        }

        if (requestDto.getStartDateTime().isBefore(LocalDateTime.now())
                || requestDto.getEndDateTime().isBefore(requestDto.getStartDateTime())) {
            throw new ApiException(ApiErrorPayload.builder().status(HttpStatus.BAD_REQUEST)
                    .message("Start date time cannot be in the past")
                    .build()
            );
        }

        if (requestDto.getEndDateTime().isBefore(LocalDateTime.now())
                || requestDto.getEndDateTime().isBefore(requestDto.getStartDateTime())) {
            throw new ApiException(ApiErrorPayload.builder().status(HttpStatus.BAD_REQUEST)
                    .message("End date time cannot be in the past")
                    .build()
            );
        }

        Activity updateActivity = m_activityRepo.save(ActivityUpdateRequestDto.from(id, requestDto));
        return ActivityDto.of(updateActivity);
    }
}
