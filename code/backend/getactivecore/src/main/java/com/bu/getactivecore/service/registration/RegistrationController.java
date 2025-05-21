package com.bu.getactivecore.service.registration;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.bu.getactivecore.service.registration.api.RegistrationApi;
import com.bu.getactivecore.service.registration.entity.RegistrationRequestDto;
import com.bu.getactivecore.service.registration.entity.RegistrationResponseDto;
import lombok.extern.slf4j.Slf4j;

/**
 * Entry point for all registration related requests.
 */
@Slf4j
@RestController
@RequestMapping(value = "/v1")
@CrossOrigin(origins = "*")
public class RegistrationController {

    @Autowired
    private RegistrationApi m_registrationApi;



    @PostMapping(path = "/register", consumes = "application/json")
    public RegistrationResponseDto registerUser(
            @RequestBody(required = true) RegistrationRequestDto registerUserDto) {
        log.debug("Got request: /v1/register");
        return m_registrationApi.registerUser(registerUserDto);
    }
}
