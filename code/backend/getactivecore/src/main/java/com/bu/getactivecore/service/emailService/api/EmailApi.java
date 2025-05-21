package com.bu.getactivecore.service.emailService.api;

import lombok.NonNull;

public interface EmailApi {
    
    /**
     * Sends a verification email to the user.
     *
     * @param email The email address of the user.
     */
    void sendVerificationEmail(@NonNull String email);
    
}
