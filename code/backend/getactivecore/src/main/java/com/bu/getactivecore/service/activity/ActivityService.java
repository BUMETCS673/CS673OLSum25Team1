package com.bu.getactivecore.service.activity;

import com.bu.getactivecore.model.activity.Activity;
import com.bu.getactivecore.model.activity.UserActivityRole;
import com.bu.getactivecore.repository.ActivityRepository;
import com.bu.getactivecore.repository.UserActivityRoleRepository;
import com.bu.getactivecore.service.activity.api.ActivityApi;
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
    public List<Activity> getActivityByName(String activityName) {
        return m_activityRepo.findByNameContaining(activityName);
    }


    @Override
    public List<Activity> getAllActivities() {
        return m_activityRepo.findAll();
    }

    @Override
    public Activity createActivity(String userId, Activity activity) {
        Activity createdActivity  = m_activityRepo.save(activity);
        UserActivityRole userActivityRole = UserActivityRole.builder()
                .userId(userId)
                .activityId(createdActivity.getId())
                .role(UserActivityRole.RoleType.ADMIN)
                .build();
       m_userActivityRoleRepo.save(userActivityRole);
       return createdActivity;
    }
}
