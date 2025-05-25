package com.bu.getactivecore.shared;

/**
 * List of errors which are used in the application.
 */
public enum ErrorCode {

    EMAIL_SEND_FAILED("EMAIL_SEND_FAILED", "Failed to send verification email"),
    EMAIL_USERNAME_TAKEN("EMAIL_USERNAME_TAKEN", "The provided email or username is already taken"),
    INTERNAL_SERVER_ERROR("INTERNAL_SERVER_ERROR", "An unexpected error occurred"),
    DATA_STRUCTURE_INVALID("DATA_STRUCTURE_INVALID", "The requested data structure is invalid"),
    EMAIL_INVALID("EMAIL_INVALID", "The provided email address is invalid"),
    TOKEN_INVALID("TOKEN_INVALID", "The provided token is invalid"),
    RESOURCE_ACCESS_DENIED("RESOURCE_ACCESS_DENIED", "Access to the requested resource is denied"),
    UNSUPPORTED_MEDIA_TYPE("UNSUPPORTED_MEDIA_TYPE", "The provided media type is not supported"),
    UNSUPPORTED_OPERATION("UNSUPPORTED_OPERATION", "This endpoint does not support the requested operation"),
    WRONG_CREDENTIALS("INVALID_CREDENTIALS", "The provided username and password are invalid");

    private final String m_code;
    private final String m_details;

    /**
     * Constructor for the error codes.
     *
     * @param code    error code
     * @param details detailed description of the error
     */
    ErrorCode(String code, String details) {
        m_code = code;
        m_details = details;
    }

    public String getCode() {
        return m_code;
    }

    public String getDetails() {
        return m_details;
    }
}


