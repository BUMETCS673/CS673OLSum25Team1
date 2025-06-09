package com.getactive.service;

import com.getactive.model.Activity;
import com.getactive.repository.ActivityRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ActivityService {
    private final ActivityRepository repository;

    public ActivityService(ActivityRepository repository) {
        this.repository = repository;
    }

    public List<Activity> getAll() {
        return repository.findAll();
    }

    public Activity create(Activity activity) {
        return repository.save(activity);
    }
}
