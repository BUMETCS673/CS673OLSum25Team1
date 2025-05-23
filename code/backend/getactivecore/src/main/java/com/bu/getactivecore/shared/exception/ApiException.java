package com.bu.getactivecore.shared.exception;

import com.bu.getactivecore.shared.ErrorCode;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class ApiException extends RuntimeException {

    private final ErrorCode errorCode;
    private final HttpStatus status;
    private final String errorMessage;

    public ApiException(HttpStatus httpStatus, ErrorCode errorCode, String errorMessage, Throwable debugMessage) {
        super(debugMessage.getLocalizedMessage());
        this.errorCode = errorCode;
        this.status = httpStatus;
        this.errorMessage = errorMessage;
    }

    public ApiException(HttpStatus httpStatus, ErrorCode errorCode, String errorMessage) {
        super(errorMessage);
        this.errorCode = errorCode;
        this.status = httpStatus;
        this.errorMessage = errorMessage;
    }
}