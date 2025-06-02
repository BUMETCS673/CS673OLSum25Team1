package com.bu.getactivecore.service.activity;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.bu.getactivecore.model.activity.Activity;
import com.bu.getactivecore.model.activity.RoleType;
import com.bu.getactivecore.model.activity.UserActivity;
import com.bu.getactivecore.model.users.Users;
import com.bu.getactivecore.repository.ActivityRepository;
import com.bu.getactivecore.repository.UserActivityRepository;
import com.bu.getactivecore.repository.UserRepository;
import com.bu.getactivecore.shared.exception.ApiException;

import jakarta.persistence.EntityNotFoundException;

@ExtendWith(MockitoExtension.class)
public class ActivityServiceTest {

    @Mock
    private ActivityRepository activityRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private UserActivityRepository userActivityRepository;

    @InjectMocks
    private ActivityService activityService;

    private String userId = "1";

    private String activityId = "1";

    @Test
    public void deleteActivityWithInValidActivityId(){
        when(activityRepository.findById(activityId)).thenReturn(Optional.empty());

        assertThrows(ApiException.class, () -> activityService.deleteActivity(userId, activityId));

        verify(userActivityRepository, never()).deleteByActivityId(activityId);
        
        verify(activityRepository, never()).deleteById(activityId);
    }

    @Test
    public void deleteActivityWithInvalidUserId(){
        when(activityRepository.findById(activityId)).thenReturn(Optional.of(new Activity()));

        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        assertThrows(ApiException.class, () -> activityService.deleteActivity(userId, activityId));

        verify(userActivityRepository, never()).deleteByActivityId(activityId);
        
        verify(activityRepository, never()).deleteById(activityId);
    }


    @Test
    public void deleteActivitySuccessfully(){
        when(activityRepository.findById(activityId)).thenReturn(Optional.of(new Activity()));

        when(userRepository.findById(userId)).thenReturn(Optional.of(new Users()));

        UserActivity userActivity = UserActivity.builder().activityId(activityId).userId(userId).role(RoleType.ADMIN).build();

        when(userActivityRepository.findByUserIdAndActivityId(userId, activityId)).thenReturn(Optional.of(userActivity));

        activityService.deleteActivity(userId, activityId);
        
        verify(userActivityRepository).deleteByActivityId(activityId);
       
        verify(activityRepository).deleteById(activityId);
    }

}
