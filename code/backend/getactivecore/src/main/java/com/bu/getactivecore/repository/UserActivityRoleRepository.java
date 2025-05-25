package com.bu.getactivecore.repository;

import com.bu.getactivecore.model.activity.UserActivityRole;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

/**
 * Repository interface for managing user activity roles.
 */
public interface UserActivityRoleRepository extends JpaRepository<UserActivityRole, String> {

    /**
     * Finds the {@link UserActivityRole} based on given parameters.
     *
     * @param userId     The ID of the user to search its activity role.
     * @param activityId The ID of the activity to search the given user's role in.
     * @return {@link UserActivityRole} if found, otherwise {@link Optional#empty()}.
     */
    Optional<UserActivityRole> findByUserIdAndActivityId(String userId, String activityId);
}