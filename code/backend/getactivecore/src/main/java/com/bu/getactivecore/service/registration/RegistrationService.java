package com.bu.getactivecore.service.registration;

import com.bu.getactivecore.service.emailService.api.EmailApi;
import com.bu.getactivecore.service.registration.api.RegistrationApi;
import com.bu.getactivecore.service.registration.entity.RegistrationRequestDto;
import com.bu.getactivecore.service.registration.entity.RegistrationResponseDto;
import org.springframework.stereotype.Service;

/**
 * Core logic for managing user registration.
 */
@Service
public class RegistrationService implements RegistrationApi {

    private final EmailApi m_emailApi;

    public RegistrationService(EmailApi emailApi) {
        m_emailApi = emailApi;
    }

    @Override
    public RegistrationResponseDto registerUser(RegistrationRequestDto registerUserDto) {
        m_emailApi.sendVerificationEmail(registerUserDto.getEmail());
        return RegistrationResponseDto.builder()
                .status(RegistrationResponseDto.RegistrationStatus.SUCCESS).build();
    }

}
