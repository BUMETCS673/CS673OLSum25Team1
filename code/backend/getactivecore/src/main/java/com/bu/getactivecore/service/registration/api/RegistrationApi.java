package com.bu.getactivecore.service.registration.api;

import com.bu.getactivecore.service.registration.entity.RegistrationRequestDto;
import com.bu.getactivecore.service.registration.entity.RegistrationResponseDto;

/**
 * Interface for managing user registration.
 */
public interface RegistrationApi {
    RegistrationResponseDto registerUser(RegistrationRequestDto registerUserDto);
}
