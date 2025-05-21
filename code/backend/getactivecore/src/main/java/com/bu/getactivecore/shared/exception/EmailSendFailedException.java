package com.bu.getactivecore.shared.exception;

/**
 * Exception thrown when email cannot be sent during registration.
 */
public class EmailSendFailedException extends RegistrationException {
    public EmailSendFailedException(String email) {
        super("INVALID_EMAIL", "Could not send a verification email to " + email);
    }
}
