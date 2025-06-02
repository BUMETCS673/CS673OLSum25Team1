package com.bu.getactivecore.repository;

import com.bu.getactivecore.model.activity.RoleType;
import com.bu.getactivecore.model.activity.UserActivity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

/**
 * Repository interface for managing user activity roles.
 */
public interface UserActivityRepository extends JpaRepository<UserActivity, String> {

    /**
     * Finds the {@link UserActivity} based on given parameters.
     *
     * @param userId     The ID of the user to search its activity role.
     * @param activityId The ID of the activity to search the given user's role in.
     * @return {@link UserActivity} if found, otherwise {@link Optional#empty()}.
     */
    Optional<UserActivity> findByUserIdAndActivityId(String userId, String activityId);

    Optional<List<UserActivity>> findByActivityIdAndRole(String activityId, RoleType role);

    void deleteByActivityId(String activityId);
}