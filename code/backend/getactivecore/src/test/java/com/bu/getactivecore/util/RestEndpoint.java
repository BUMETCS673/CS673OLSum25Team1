package com.bu.getactivecore.util;

/**
 * Helper enum which contains REST API endpoints used in tests.
 */
public enum RestEndpoint {
    REGISTER("/v1/register"),
    LOGIN("/v1/login"),
    ACTIVITY("/v1/activity");

    private final String endpoint;


    RestEndpoint(String endpoint) {
        this.endpoint = endpoint;
    }

    public String get() {
        return endpoint;
    }
}
