package com.bu.getactivecore.repository;

import com.bu.getactivecore.model.activity.Activity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

/**
 * Repository interface for Activity entity.
 */
public interface ActivityRepository extends JpaRepository<Activity, String> {
    List<Activity> findByNameContaining(String name);
}
