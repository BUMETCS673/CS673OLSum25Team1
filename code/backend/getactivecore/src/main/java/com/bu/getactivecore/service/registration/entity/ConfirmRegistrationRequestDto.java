package com.bu.getactivecore.service.registration.entity;


import jakarta.validation.constraints.NotBlank;
import lombok.Value;

@Value
public class ConfirmRegistrationRequestDto {

    @NotBlank(message = "Token cannot be blank")
    String token;
}
