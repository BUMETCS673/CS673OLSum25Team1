package com.bu.getactivecore.service.activity;

import com.bu.getactivecore.model.users.UserPrincipal;
import com.bu.getactivecore.service.activity.api.ActivityApi;
import com.bu.getactivecore.service.activity.entity.ActivityCreateRequestDto;
import com.bu.getactivecore.service.activity.entity.ActivityDeleteRequestDto;
import com.bu.getactivecore.service.activity.entity.ActivityDto;
import com.bu.getactivecore.service.activity.entity.ActivityResponseDto;
import com.bu.getactivecore.service.activity.entity.ActivityUpdateRequestDto;

import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;

import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.data.domain.Sort;

import java.util.HashMap;
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
     * @param page current page number
     * @param size page size
     * @param sortBy sort by fields
     * @param ascending ascending or descending
     * @return Page of activities
     */
    @GetMapping("/activities")
    public ResponseEntity<Page<ActivityDto>> getActivities(@RequestParam(name = "page", defaultValue = "0") int page, 
                                           @RequestParam(name = "size", defaultValue = "10") int size,
                                           @RequestParam(defaultValue = "id") String sortBy,
                                           @RequestParam(defaultValue = "true") boolean ascending
                                           ) {
        Sort sort = ascending ? Sort.by(sortBy).ascending() : Sort.by(sortBy).descending();  
        return ResponseEntity.ok(m_activityApi.getAllActivities(page, size, sort));                                         
    }

    /**
     * Get activities by name.
     *
     * @param name Name of the activity
     * @param page current page number
     * @param size page size
     * @param sortBy sort by fields
     * @param ascending ascending or descending
     * @return Page of activities matching the name
     */
    @GetMapping("/activity/{name}")
    public ResponseEntity<Page<ActivityDto>> getActivityByName(@PathVariable String name, 
                                               @RequestParam(name = "page", defaultValue = "0") int page, 
                                               @RequestParam(name = "size", defaultValue = "10") int size,
                                               @RequestParam(defaultValue = "id") String sortBy,
                                               @RequestParam(defaultValue = "true") boolean ascending
    ) {
        Sort sort = ascending ? Sort.by(sortBy).ascending() : Sort.by(sortBy).descending();  
        return ResponseEntity.ok(m_activityApi.getActivityByName(name, page, size, sort));                
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
     @ai-generated, Tool: Google Gemini,
     Prompt: "how to create a Post API in spring boot",
     Generated on: 2025-05-22,
     Modified by: Jin Hao,
     Modifications: change the return type and add validation to the input,
     Verified: ✅ Unit tested, reviewed
     */

    /**
     * create an activity
     *
     * @param requestDto requested activity
     * @return an activity
     */
    @Transactional
    @PostMapping("/activity")
    public ResponseEntity<Object> createActivity(@AuthenticationPrincipal UserPrincipal user, @RequestBody @Valid ActivityCreateRequestDto requestDto) {
        String userId = user.getUserDto().getUserId();
        m_activityApi.createActivity(userId, requestDto);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @Transactional
    @DeleteMapping("/activity/{id}")
    @PreAuthorize("@activityPermissionEvaluator.isAuthorizedToUpdateActivity(authentication, #id)")
    public ResponseEntity<Object> deleteActivity(@AuthenticationPrincipal UserPrincipal user, @PathVariable String id, @Valid ActivityDeleteRequestDto requestDto) {
        String userId = user.getUserDto().getUserId();
        m_activityApi.deleteActivity(userId, id, requestDto);
        return ResponseEntity.status(HttpStatus.OK).build();
    }


    @PutMapping("/activity/{id}")
    @PreAuthorize("@activityPermissionEvaluator.isAuthorizedToUpdateActivity(authentication, #id)")
    public ResponseEntity<ActivityDto> updateActivity(@AuthenticationPrincipal UserPrincipal user, @PathVariable String id, @Valid @RequestBody ActivityUpdateRequestDto request) {
         String userId = user.getUserDto().getUserId();
         return ResponseEntity.ok(m_activityApi.updateActivity(userId, id, request));
    }
}
