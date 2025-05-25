package com.bu.getactivecore.config;


import com.bu.getactivecore.shared.ApiError;
import com.bu.getactivecore.shared.ApiErrorResponse;
import com.bu.getactivecore.shared.ErrorCode;
import com.bu.getactivecore.shared.exception.ResourceAccessDeniedException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.List;
import java.util.Map;

/**
 * A custom {@link AccessDeniedHandler} implementation that handles access denied exceptions by returning a structured
 * JSON response with error details.
 */
@Component
public class CustomAccessDeniedHandler implements AccessDeniedHandler {

    private final ObjectMapper objectMapper; // Jackson mapper to serialize ApiErrorResponse

    public CustomAccessDeniedHandler(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    @Override
    public void handle(HttpServletRequest request, HttpServletResponse response, AccessDeniedException ex) throws IOException {
        Map<String, List<String>> validationErrors = null;
        if (ex instanceof ResourceAccessDeniedException resourceEx) {
            validationErrors = resourceEx.getValidationErrors();
        }
        ApiError apiError = ApiError.from(
                ErrorCode.RESOURCE_ACCESS_DENIED,
                HttpStatus.FORBIDDEN,
                ex.getMessage() != null ? "Access Denied: " + ex.getMessage() : "Access Denied",
                validationErrors,
                ex
        );
        ApiErrorResponse errorResponse = new ApiErrorResponse(apiError);

        response.setStatus(HttpStatus.FORBIDDEN.value());
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        objectMapper.writeValue(response.getWriter(), errorResponse);
    }
}

