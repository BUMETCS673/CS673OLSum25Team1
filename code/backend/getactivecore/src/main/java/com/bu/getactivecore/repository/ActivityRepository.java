package com.bu.getactivecore.repository;

import com.bu.getactivecore.model.activity.Activity;
import com.bu.getactivecore.model.activity.UserActivity;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

/**
 * Repository interface for Activity entity.
 */
public interface ActivityRepository extends JpaRepository<Activity, String> {
    Page<Activity> findByNameContaining(String name, Pageable pageable);

    Optional<Activity> findByName(String name);

    @Query("SELECT a FROM activities a ORDER BY SIZE(a.users) DESC")
    Page<Activity> findAllSortedByPopularity(Pageable pageable);
}
