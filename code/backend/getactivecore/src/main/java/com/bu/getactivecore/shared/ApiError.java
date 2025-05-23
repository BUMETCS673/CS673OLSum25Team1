package com.bu.getactivecore.shared;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import org.springframework.http.HttpStatus;

import java.time.LocalDateTime;

@Data
public final class ApiError {

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd-MM-yyyy hh:mm:ss")
    private final LocalDateTime timestamp;
    private HttpStatus status;
    private ErrorCode errorCode;
    private String message;
    private String debugMessage;

    private ApiError() {
        timestamp = LocalDateTime.now();
    }

    public ApiError(HttpStatus status) {
        this();
        this.status = status;
    }

    public ApiError(ErrorCode errorCode, HttpStatus status, Throwable ex) {
        this();
        this.errorCode = errorCode;
        this.status = status;
        this.message = "Unexpected error";
        this.debugMessage = ex.getLocalizedMessage();
    }

    public ApiError(ErrorCode errorCode, HttpStatus status, String message, Throwable ex) {
        this();
        this.errorCode = errorCode;
        this.status = status;
        this.message = message;
        this.debugMessage = ex.getLocalizedMessage();
    }
}