package com.bu.getactivecore.service.users.entity;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Value;

/**
 * The DTO for user registration request.
 */
@Value
public class RegistrationRequestDto {

    @NotBlank(message = "Email cannot be blank")
    @ValidBuEmail
    String email;

    @NotBlank(message = "Username cannot be blank")
    @Size(min = 2, message = "Username must be at least 2 characters")
    @Size(max = 20, message = "Username can be most 20 characters")
    String username;

    @NotBlank(message = "Password cannot be blank")
    @Size(min = 2, message = "Password length must be at least 2 characters")
    @Size(max = 32, message = "Password length can be most 32 characters")
    String password;
}
