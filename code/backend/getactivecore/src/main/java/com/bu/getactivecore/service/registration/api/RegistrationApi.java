package com.bu.getactivecore.service.registration.api;

import com.bu.getactivecore.service.registration.entity.ConfirmRegistrationRequestDto;
import com.bu.getactivecore.service.registration.entity.RegistrationRequestDto;
import com.bu.getactivecore.service.registration.entity.RegistrationResponseDto;
import com.bu.getactivecore.shared.exception.ApiException;
import jakarta.validation.Valid;

/**
 * Interface for managing user registration operations.
 */
public interface RegistrationApi {

    /**
     * Registers a new user with the provided registration details.
     *
     * @param registerUserDto containing user registration info.
     * @return {@link RegistrationResponseDto } indicating registration result.
     * @throws ApiException If registration fails
     */
    RegistrationResponseDto registerUser(RegistrationRequestDto registerUserDto) throws ApiException;

    /**
     * Verifies the user registration by checking the provided verification details.
     *
     * @param verificationDto details for verifying the registration.
     * @return {@link RegistrationResponseDto} indicating the result of the verification registration.
     */
    RegistrationResponseDto confirmRegistration(@Valid ConfirmRegistrationRequestDto verificationDto);
}
