package com.bu.getactivecore.util;

/**
 * Helper enum which contains REST API endpoints used in tests.
 */
public enum RestEndpoint {
    ACTIVITY("/v1/activity"),
    CONFIRM_REGISTRATION("/v1/register/confirm"),
    LOGIN("/v1/login"),
    REGISTER("/v1/register");

    private final String endpoint;


    RestEndpoint(String endpoint) {
        this.endpoint = endpoint;
    }

    public String get() {
        return endpoint;
    }
}
