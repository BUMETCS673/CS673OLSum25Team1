package com.bu.getactivecore.service.users.entity;

import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class RegistrationResponseDto {

    RegistrationStatus status;


    public enum RegistrationStatus {
        SUCCESS, PENDING
    }
}
