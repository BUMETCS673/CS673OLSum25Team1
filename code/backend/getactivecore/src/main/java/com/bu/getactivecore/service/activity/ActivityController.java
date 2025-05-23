package com.bu.getactivecore.service.activity;

import com.bu.getactivecore.error.ResourceNotFoundException;
import com.bu.getactivecore.model.Activity;

import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.List;
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

    @Autowired
    private ActivityApi m_activityApi;

    /**
     * Get all activities.
     *
     * @return List of activities
     */
    @GetMapping("/activities")
    public List<Activity> getActivities() {
        log.info("Got request: /v1/activities");
        return m_activityApi.getAllActivities();
    }

    /**
     * Get activities by name.
     *
     * @param activityName Name of the activity
     * @return List of activities matching the name
     */
    @GetMapping("/activity/{name}")
    public List<Activity> getActivityByName(@PathVariable String name) {
        List<Activity> activities = m_activityApi.getActivitiesByName(name);
        if(activities.isEmpty()){
           throw new ResourceNotFoundException("Activity cannot be found");
        }
        return m_activityApi.getActivitiesByName(name);
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
    public ResponseEntity<Object> createActivity(@RequestBody @Valid Activity activity) throws Exception {
        m_activityApi.createActivity(activity);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }
}
