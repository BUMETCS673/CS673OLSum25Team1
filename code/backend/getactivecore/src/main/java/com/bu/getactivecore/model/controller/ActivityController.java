package com.getactive.controller;

import com.getactive.model.Activity;
import com.getactive.service.ActivityService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/activities")
@CrossOrigin(origins = "*")  // Enable Cross-Origin for frontend requests
public class ActivityController {
    private final ActivityService service;

    public ActivityController(ActivityService service) {
        this.service = service;
    }

    @GetMapping
    public List<Activity> getAllActivities() {
        return service.getAll();
    }

    @PostMapping
    public Activity createActivity(@RequestBody Activity activity) {
        return service.create(activity);
    }
}
