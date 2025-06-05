package com.bu.getactivecore.service.activity;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;

import com.bu.getactivecore.model.activity.Activity;
import com.bu.getactivecore.model.activity.RoleType;
import com.bu.getactivecore.model.activity.UserActivity;
import com.bu.getactivecore.repository.ActivityRepository;
import com.bu.getactivecore.repository.UserActivityRepository;
import com.bu.getactivecore.repository.UserRepository;
import com.bu.getactivecore.service.activity.entity.ActivityDeleteRequestDto;
import com.bu.getactivecore.shared.exception.ApiException;

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

        assertThrows(ApiException.class, () -> activityService.deleteActivity(userId, activityId, ActivityDeleteRequestDto.builder().force(false).build()));

        verify(userActivityRepository, never()).deleteByActivityId(activityId);
        
        verify(activityRepository, never()).deleteById(activityId);
    }


    @Test
    public void deleteActivityWithForceSetToTrue(){
        when(activityRepository.findById(activityId)).thenReturn(Optional.of(new Activity()));

        ActivityDeleteRequestDto requestDTO = ActivityDeleteRequestDto.builder().force(false).build();
        activityService.deleteActivity(userId, activityId, requestDTO);
        
        verify(userActivityRepository).deleteByActivityId(activityId);
       
        verify(activityRepository).deleteById(activityId);
    }


    @Test
    public void deleteActivityWithForceSetToFalseAndHasParticipantsInActivity(){
        when(activityRepository.findById(activityId)).thenReturn(Optional.of(new Activity()));

        when(userActivityRepository.findByActivityIdAndRole(activityId, RoleType.PARTICIPANT)).thenReturn(Optional.of(List.of(new UserActivity())));

        assertThrows(ApiException.class, () -> activityService.deleteActivity(userId, activityId, ActivityDeleteRequestDto.builder().force(false).build()));

        verify(userActivityRepository, never()).deleteByActivityId(activityId);
       
        verify(activityRepository, never()).deleteById(activityId);
    }

    @Test
    public void deleteActivitySuccessfully(){
        when(activityRepository.findById(activityId)).thenReturn(Optional.of(new Activity()));

        when(userActivityRepository.findByActivityIdAndRole(activityId, RoleType.PARTICIPANT)).thenReturn(Optional.empty());


        activityService.deleteActivity(userId, activityId, ActivityDeleteRequestDto.builder().force(false).build());
        
        verify(userActivityRepository).deleteByActivityId(activityId);
       
        verify(activityRepository).deleteById(activityId);
    }

    @Test
    public void testGetActivities(){
        PageRequest pageable = PageRequest.of(0, 10, Sort.by("id").ascending());
        Page<Activity> page = new PageImpl<>(List.of(Activity.builder().build()), pageable, 1);
        when(activityRepository.findAll(pageable)).thenReturn(page);

        activityService.getAllActivities(0, 10, Sort.by("id").ascending());

        verify(activityRepository).findAll(pageable);
    }

    @Test
    public void testGetActivitiesByName(){
        PageRequest pageable = PageRequest.of(0, 10, Sort.by("id").ascending());
        Page<Activity> page = new PageImpl<>(List.of(Activity.builder().build()), pageable, 1);
        when(activityRepository.findByNameContaining("Rock Climbing", pageable)).thenReturn(page);

        activityService.getActivityByName("Rock Climbing",0, 10, Sort.by("id").ascending());

        verify(activityRepository).findByNameContaining("Rock Climbing", pageable);
    }

    @Test
    public void deleteActivitySuccessfully(){
        when(activityRepository.findById(activityId)).thenReturn(Optional.of(new Activity()));

        when(userActivityRepository.findByActivityIdAndRole(activityId, RoleType.PARTICIPANT)).thenReturn(Optional.empty());


        activityService.deleteActivity(userId, activityId, ActivityDeleteRequestDto.builder().force(false).build());
        
        verify(userActivityRepository).deleteByActivityId(activityId);
       
        verify(activityRepository).deleteById(activityId);
    }


}
