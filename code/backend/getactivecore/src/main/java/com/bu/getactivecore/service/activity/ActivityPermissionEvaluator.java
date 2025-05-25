package com.bu.getactivecore.service.activity;

import com.bu.getactivecore.model.activity.UserActivityRole;
import com.bu.getactivecore.model.users.UserPrincipal;
import com.bu.getactivecore.model.users.Users;
import com.bu.getactivecore.repository.UserActivityRoleRepository;
import com.bu.getactivecore.shared.exception.ResourceAccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

/**
 * Contains methods to evaluate user permissions for activities.
 * It is used in Controllers to check if a user has admin permissions for a specific activity.
 */
@Component("activityPermissionEvaluator")
public class ActivityPermissionEvaluator {

    private final UserActivityRoleRepository roleRepo;

    /**
     * Constructs the permission evaluator with the provided {@link UserActivityRoleRepository}.
     *
     * @param roleRepo used to fetch user roles for activities
     */
    public ActivityPermissionEvaluator(UserActivityRoleRepository roleRepo) {
        this.roleRepo = roleRepo;
    }

    /**
     * Checks if the user has admin permissions for a specific activity.
     *
     * @param authentication the current user's authentication object
     * @param activityId     the ID of the activity to check permissions for
     * @return true if the user is an admin for the activity, otherwise exception is thrown
     * @throws ResourceAccessDeniedException if the user is not an admin of the activity
     */
    public boolean isAuthorizedToUpdateActivity(Authentication authentication, String activityId) {
        Users user = ((UserPrincipal) authentication.getPrincipal()).getUser();
        String userId = user.getUserId();
        return roleRepo.findByUserIdAndActivityId(userId, activityId)
                .map(role -> UserActivityRole.RoleType.ADMIN == role.getRole())
                .orElseThrow(() -> {
                    String reason = String.format("User %s is not an admin of activity %s", userId, activityId);
                    Map<String, List<String>> validationErrors = Map.of(
                            "permission", List.of("User is not the admin of given activity")
                    );
                    return new ResourceAccessDeniedException(reason, validationErrors);
                });
    }
}
