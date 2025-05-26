package com.bu.getactivecore.service.activity;

import com.bu.getactivecore.model.activity.Activity;
import com.bu.getactivecore.model.users.UserPrincipal;
import com.bu.getactivecore.model.users.Users;
import com.bu.getactivecore.service.activity.api.ActivityApi;
import com.bu.getactivecore.service.activity.entity.ActivityCreateRequestDto;
import com.bu.getactivecore.service.activity.entity.ActivityDto;
import com.bu.getactivecore.service.activity.entity.ActivityResponseDto;
import com.bu.getactivecore.service.activity.entity.ActivityUpdateRequestDto;
import com.bu.getactivecore.shared.exception.ApiException;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Entry point for all activity-related APIs.
 */
@Slf4j
@RestController
@RequestMapping("/v1")
@CrossOrigin(origins = "*")
public class ActivityController {

    private final ActivityApi m_activityApi;

    /**
     * Constructs the ActivityController.
     *
     * @param activityApi used to fetch and manage activities
     */
    public ActivityController(ActivityApi activityApi) {
        m_activityApi = activityApi;
    }

    /**
     * Get all activities.
     *
     * @return List of activities
     */
    @GetMapping("/activities")
    public List<ActivityDto> getActivities() {
        List<Activity> activities = m_activityApi.getAllActivities();
        List<ActivityDto> activityDto = activities.stream().map(activity ->ActivityDto.of(activity)).toList();
        return activityDto;
    }

    /**
     * Get activities by name.
     *
     * @param activityName Name of the activity
     * @return List of activities matching the name
     */
    @GetMapping("/activity/{name}")
    public List<ActivityDto> getActivityByName(@PathVariable String name) {
        List<Activity> activities = m_activityApi.getActivityByName(name);
        List<ActivityDto> activityDto = activities.stream().map(activity ->ActivityDto.of(activity)).toList();
        return activityDto;
    }


    @PutMapping("/activity")
    @PreAuthorize("@activityPermissionEvaluator.isAuthorizedToUpdateActivity(authentication, #request.activityId)")
    public ActivityResponseDto updateActivity(@Valid @RequestBody ActivityUpdateRequestDto request) {
        log.info("Got request: /v1/activity/update");

        // TODO implement the logic to update an activity
        return new ActivityResponseDto(
                ActivityDto.builder().build()
        );
    }

    @PutMapping("/activity/join")
    public ActivityResponseDto join(@RequestBody ActivityCreateRequestDto request) {
        log.info("Got request: /v1/activity/join");

        // TODO implement the logic to join an activity
        return new ActivityResponseDto(
                ActivityDto.builder().build()
        );
    }


    @GetMapping("/health")
    public ResponseEntity<Map<String, String>> healthCheck() {
        Map<String, String> response = new HashMap<>();
        response.put("status", "UP");
        response.put("message", "Service is running");
        response.put("timestamp", String.valueOf(System.currentTimeMillis()));
        
        return ResponseEntity.ok(response);
    }
    
    /**
    @ai-generated,
    Tool: Google Gemini,
    Prompt: "how to create a Post API in spring boot",
    Generated on: 2025-05-22,
    Modified by: Jin Hao,
    Modifications: change the return type and add validation to the input,
    Verified: ✅ Unit tested, reviewed
    */

    /**
     * create an activity
     *
     * @param activity requested activity
     * @return an activity
     */
    @PostMapping("/activity")
    public ResponseEntity<Object> createActivity(@AuthenticationPrincipal UserPrincipal user, @RequestBody @Valid ActivityCreateRequestDto request) throws Exception {
        Users user1 = user.getUser();
        String userId = user1.getUserId();
        m_activityApi.createActivity(userId, ActivityCreateRequestDto.from(request));
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }
}
