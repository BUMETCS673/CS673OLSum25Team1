package com.bu.getactivecore.service.activity;

import com.bu.getactivecore.model.Activity;

import java.util.List;

/**
 * Interface for managing activities.
 */
public interface ActivityApi {
    List<Activity> getAllActivities();

    List<Activity> getActivitiesByName(String name);

    Activity createActivity(Activity activity);
}
