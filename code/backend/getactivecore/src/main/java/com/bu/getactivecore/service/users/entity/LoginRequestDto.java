package com.bu.getactivecore.service.users.entity;

import jakarta.validation.constraints.NotBlank;
import lombok.Value;

/**
 * The DTO for user login requests.
 */
public record LoginRequestDto(@NotBlank(message = "Username must not be blank") String username,
                              @NotBlank(message = "Password must not be blank") String password) {

}
