package com.bu.getactivecore.service.email.api;

import com.bu.getactivecore.shared.exception.ApiException;
import lombok.NonNull;

public interface EmailApi {

    /**
     * Sends a verification email to the user.
     *
     * @param email           The email address of the user.
     * @param registrationUrl The URL for the user to verify their email.
     */
    void sendVerificationEmail(@NonNull String email, @NonNull String registrationUrl) throws ApiException;

}
