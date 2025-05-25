package com.bu.getactivecore.shared.exception;

import com.bu.getactivecore.shared.ErrorCode;
import lombok.Getter;
import org.springframework.http.HttpStatus;

/**
 * ApiException is a custom exception class used to represent application-specific
 * errors in a structured and consistent manner.
 *
 * <p>This exception is typically thrown when an error occurs that needs to be
 * communicated to the client with additional details such as an error code,
 * HTTP status, and a user-friendly error message.</p>
 */
@Getter
public class ApiException extends RuntimeException {

    private final ErrorCode errorCode;
    private final HttpStatus status;
    private final String errorMessage;

    /**
     * Constructs a new ApiException.
     *
     * @param httpStatus   the HTTP status associated with the error
     * @param errorCode    the application-specific error code
     * @param errorMessage a user-friendly error message
     * @param debugMessage a detailed debug message (usually for logging purposes)
     */
    public ApiException(HttpStatus httpStatus, ErrorCode errorCode, String errorMessage, Throwable debugMessage) {
        super(debugMessage.getLocalizedMessage());
        this.errorCode = errorCode;
        this.status = httpStatus;
        this.errorMessage = errorMessage;
    }

    /**
     * Constructs a new ApiException.
     *
     * @param httpStatus   the HTTP status associated with the error
     * @param errorCode    the application-specific error code
     * @param errorMessage a user-friendly error message
     * @param debugMessage a detailed debug message (for logging purposes)
     */
    public ApiException(HttpStatus httpStatus, ErrorCode errorCode, String errorMessage, String debugMessage) {
        super(debugMessage);
        this.errorCode = errorCode;
        this.status = httpStatus;
        this.errorMessage = errorMessage;
    }

    /**
     * Constructs a new ApiException.
     *
     * @param httpStatus   the HTTP status associated with the error
     * @param errorCode    the application-specific error code
     * @param errorMessage a user-friendly error message
     */
    public ApiException(HttpStatus httpStatus, ErrorCode errorCode, String errorMessage) {
        super(errorMessage);
        this.errorCode = errorCode;
        this.status = httpStatus;
        this.errorMessage = errorMessage;
    }
}