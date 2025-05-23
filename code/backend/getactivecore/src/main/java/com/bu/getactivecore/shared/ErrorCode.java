package com.bu.getactivecore.shared;

/**
 * List of errors which are used in the application.
 */
public enum ErrorCode {

    INTERNAL_SERVER_ERROR("INTERNAL_SERVER_ERROR", "An unexpected error occurred"),
    INVALID_EMAIL("INVALID_EMAIL", "The provided email address is invalid"),
    INVALID_DATA_STRUCTURE("INVALID_REQUEST_DATA_STRUCTURE", "The request data structure is invalid"),
    EMAIL_USERNAME_TAKEN("EMAIL_USERNAME_TAKEN", "The provided email or username is already taken"),
    EMAIL_SEND_FAILED("EMAIL_SEND_FAILED", "Failed to send verification email");

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


