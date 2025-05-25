package com.bu.getactivecore.service.users.entity;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

/**
 * Validator for the {@link ValidBuEmail} annotation.
 * Checks for valid "@bu.edu" domain.
 */
@SuppressWarnings("java:S2692")
public class BuEmailValidator implements ConstraintValidator<ValidBuEmail, String> {

    @Override
    public boolean isValid(String email, ConstraintValidatorContext constraintValidatorContext) {
        return email != null && email.endsWith("@bu.edu") && email.indexOf('@') > 0;
    }
}
