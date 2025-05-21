package com.bu.getactivecore.shared.exception;

/**
 * Base class for exceptions related to user registration.
 */
public abstract class RegistrationException extends Exception {

    private final String errorCode;
    private final String message;

    public RegistrationException(String errorCode, String message) {
        super(message);
        this.errorCode = errorCode;
        this.message = message;
    }

    public String getErrorCode() {
        return errorCode;
    }

    public String getMessage() {
        return message;
    }

}
