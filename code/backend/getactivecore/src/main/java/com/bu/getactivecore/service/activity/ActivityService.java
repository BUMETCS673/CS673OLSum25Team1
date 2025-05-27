package com.bu.getactivecore.service.activity;

import com.bu.getactivecore.model.activity.Activity;
import com.bu.getactivecore.model.activity.RoleType;
import com.bu.getactivecore.model.activity.UserActivity;
import com.bu.getactivecore.model.users.Users;
import com.bu.getactivecore.repository.ActivityRepository;
import com.bu.getactivecore.repository.UserActivityRepository;
import com.bu.getactivecore.repository.UserRepository;
import com.bu.getactivecore.service.activity.api.ActivityApi;
import com.bu.getactivecore.service.activity.entity.ActivityDto;
import com.bu.getactivecore.shared.exception.ApiException;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

/**
 * Core logic for managing activities.
 */
@Service
public class ActivityService implements ActivityApi {

    private final UserActivityRepository m_userActivityRoleRepo;

    private final ActivityRepository m_activityRepo;

    private final UserRepository m_userRepo;

    /**
     * Constructs the ActivityService.
     *
     * @param activityRepo used to fetch and manage activities
     */
    public ActivityService(ActivityRepository activityRepo, UserActivityRepository activityRoleRepo, UserRepository userRepo) {
        m_activityRepo = activityRepo;
        m_userActivityRoleRepo = activityRoleRepo;
        m_userRepo = userRepo;
    }

     @Override
    public List<ActivityDto> getActivityByName(String activityName) {
        List<Activity> activities = m_activityRepo.findByNameContaining(activityName);
        return activities.stream().map(activity ->ActivityDto.of(activity)).toList();
    }


    @Override
    public List<ActivityDto> getAllActivities() {
        List<Activity> activities = m_activityRepo.findAll();
        return activities.stream().map(activity ->ActivityDto.of(activity)).toList();
    }

    @Override
    public void createActivity(String userId, Activity activity) {
       Optional<Activity> foundActivity = m_activityRepo.findByName(activity.getName());
       if(foundActivity.isPresent()){
          throw new ApiException(HttpStatus.BAD_REQUEST, null, "Activity name exists");
       }

       if(activity.getEndDateTime().isEqual(activity.getStartDateTime()) 
          || activity.getEndDateTime().isBefore(activity.getStartDateTime())){
           throw new ApiException(HttpStatus.BAD_REQUEST, null, "End date time cannot be on or before start date time");
       }

       Optional<Users> user = m_userRepo.findById(userId);
       if(!user.isPresent()){
           throw new ApiException(HttpStatus.BAD_REQUEST, null, "user doesn't exist");
       }

       Activity createdActivity  = m_activityRepo.save(activity);
       UserActivity userActivityRole = UserActivity.builder()
                .userId(userId)
                .activityId(createdActivity.getId())
                .role(RoleType.ADMIN)
                .build();
       m_userActivityRoleRepo.save(userActivityRole);
    }
}
