package com.bu.getactivecore.shared;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Data;
import org.springframework.http.HttpStatus;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

/**
 * Represents a structured error response for API exceptions.
 * <p>
 * This class is used by {@link com.bu.getactivecore.service.security.GlobalExceptionHandler} to build
 * consistent error responses sent back to API clients.
 */
@Data
@Builder(access = AccessLevel.PRIVATE)
public final class ApiError {


    /**
     * Timestamp when the error occurred, formatted as "dd-MM-yyyy hh:mm:ss".
     */
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd-MM-yyyy hh:mm:ss")
    @Builder.Default
    private final LocalDateTime timestamp = LocalDateTime.now();

    /**
     * HTTP status code for the error response.
     */
    private HttpStatus status;

    /**
     * Application-specific error code enum.
     */
    private ErrorCode errorCode;

    /**
     * Human-readable error message.
     */
    private String message;

    /**
     * List of validation errors, if any.
     */
    private Map<String, List<String>> validationErrors;


    /**
     * Detailed debug message, typically exception details.
     */
    private String debugMessage;


    /**
     * Creates an ApiError instance.
     *
     * @param errorCode application-specific error code
     * @param status    HTTP status of the error
     * @param message   human-readable error message
     * @param ex        exception to extract debug message from
     * @return constructed ApiError instance
     */
    public static ApiError from(ErrorCode errorCode, HttpStatus status, String message, Throwable ex) {
        return ApiError.builder()
                .errorCode(errorCode)
                .status(status)
                .debugMessage(ex.getLocalizedMessage())
                .message(message)
                .build();
    }

    /**
     * Creates an ApiError instance.
     *
     * @param errorCode application-specific error code
     * @param status    HTTP status of the error
     * @param message   human-readable error message
     * @param ex        exception to extract debug message from
     * @return constructed ApiError instance
     */
    public static ApiError from(ErrorCode errorCode, HttpStatus status, String message, Map<String, List<String>> validationErrors, Throwable ex) {
        return ApiError.builder()
                .errorCode(errorCode)
                .status(status)
                .debugMessage(ex.getLocalizedMessage())
                .message(message)
                .validationErrors(validationErrors)
                .build();
    }

    /**
     * Creates an ApiError instance.
     *
     * @param errorCode application-specific error code
     * @param status    HTTP status of the error
     * @param message   human-readable error message
     * @return constructed ApiError instance
     */
    public static ApiError from(ErrorCode errorCode, HttpStatus status, String message) {
        ApiErrorBuilder builder = ApiError.builder()
                .errorCode(errorCode)
                .status(status)
                .message(message);
        return builder.build();
    }
}