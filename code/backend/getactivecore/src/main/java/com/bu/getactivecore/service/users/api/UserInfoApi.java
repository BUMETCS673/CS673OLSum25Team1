package com.bu.getactivecore.service.users.api;

import com.bu.getactivecore.service.users.entity.RegistrationRequestDto;
import com.bu.getactivecore.service.users.entity.RegistrationResponseDto;
import com.bu.getactivecore.shared.exception.ApiException;

/**
 * Interface for managing user operations.
 */
public interface UserInfoApi {
    RegistrationResponseDto registerUser(RegistrationRequestDto registerUserDto) throws ApiException;
}
