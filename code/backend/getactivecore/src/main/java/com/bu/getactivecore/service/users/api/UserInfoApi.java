package com.bu.getactivecore.service.users.api;

import com.bu.getactivecore.service.users.entity.LoginRequestDto;
import com.bu.getactivecore.service.users.entity.LoginResponseDto;
import com.bu.getactivecore.service.users.entity.RegistrationRequestDto;
import com.bu.getactivecore.service.users.entity.RegistrationResponseDto;
import com.bu.getactivecore.shared.exception.ApiException;

/**
 * Interface for managing user operations.
 */
public interface UserInfoApi {

    /**
     * Registers a new user with the provided registration details.
     *
     * @param registerUserDto containing user registration info.
     * @return {@link RegistrationResponseDto } indicating registration result.
     * @throws ApiException If registration fails
     */
    RegistrationResponseDto registerUser(RegistrationRequestDto registerUserDto) throws ApiException;

    /**
     * Authenticates a user with the given login credentials.
     *
     * @param userDto Data Transfer Object containing login credentials.
     * @return {@link LoginResponseDto} containing authentication token on success.
     */
    LoginResponseDto loginUser(LoginRequestDto userDto);
}
