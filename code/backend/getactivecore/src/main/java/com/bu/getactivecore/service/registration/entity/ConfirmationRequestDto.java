package com.bu.getactivecore.service.registration.entity;

import jakarta.validation.constraints.NotBlank;

/**
 * Registration confirmation request DTO.
 *
 * @param token The token used to confirm user's registration.
 */
public record ConfirmationRequestDto(@NotBlank(message = "Token cannot be blank") String token) {
}
