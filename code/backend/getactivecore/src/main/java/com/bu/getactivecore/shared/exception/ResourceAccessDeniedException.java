package com.bu.getactivecore.shared.exception;

import lombok.Getter;
import org.springframework.security.access.AccessDeniedException;

import java.util.List;
import java.util.Map;

/**
 * ResourceAccessDeniedException is a custom exception that extends AccessDeniedException.
 * It is used to indicate that access to a specific resource is denied, with additional
 * validation errors provided for more context.
 *
 * <p>This exception can be thrown when a user attempts to access a resource they do not
 * have permission to access.
 */
@Getter
public class ResourceAccessDeniedException extends AccessDeniedException {
    private final Map<String, List<String>> validationErrors;

    /**
     * Constructs a ResourceAccessDeniedException with a reason and validation errors.
     *
     * @param reason           the reason for the access denial
     * @param validationErrors a map containing validation errors, if any
     */
    public ResourceAccessDeniedException(String reason, Map<String, List<String>> validationErrors) {
        super(reason);
        this.validationErrors = validationErrors;
    }
}
