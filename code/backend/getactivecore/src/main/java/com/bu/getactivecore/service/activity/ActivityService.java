package com.bu.getactivecore.service.activity;

import com.bu.getactivecore.model.activity.Activity;
import com.bu.getactivecore.model.activity.UserActivityRole;
import com.bu.getactivecore.repository.ActivityRepository;
import com.bu.getactivecore.repository.UserActivityRoleRepository;
import com.bu.getactivecore.service.activity.api.ActivityApi;
import com.bu.getactivecore.service.activity.entity.ActivityCreateRequestDto;
import com.bu.getactivecore.service.activity.entity.ActivityDto;
import com.bu.getactivecore.service.activity.entity.ActivityResponseDto;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Core logic for managing activities.
 */
@Service
public class ActivityService implements ActivityApi {

    private final UserActivityRoleRepository m_userActivityRoleRepo;

    private final ActivityRepository m_activityRepo;

    /**
     * Constructs the ActivityService.
     *
     * @param activityRepo used to fetch and manage activities
     */
    public ActivityService(ActivityRepository activityRepo, UserActivityRoleRepository activityRoleRepo) {
        m_activityRepo = activityRepo;
        m_userActivityRoleRepo = activityRoleRepo;
    }


    @Override
    public List<Activity> getAllActivities() {
        return m_activityRepo.findAll();
    }

    @Override
    public List<Activity> getActivityByName(String activityName) {
        return m_activityRepo.findByActivityNameContaining(activityName);
    }

    @Override
    public ActivityResponseDto createActivity(String authenticatedUserId, ActivityCreateRequestDto request) {
        Activity activity = m_activityRepo.save(ActivityDto.from(request));
        UserActivityRole userActivityRole = UserActivityRole.builder()
                .userId(authenticatedUserId)
                .activityId(activity.getActivityId())
                .role(UserActivityRole.RoleType.ADMIN)
                .build();
        m_userActivityRoleRepo.save(userActivityRole);
        return new ActivityResponseDto(ActivityDto.of(activity));
    }
}
