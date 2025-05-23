package com.bu.getactivecore.service.users;

import com.bu.getactivecore.service.users.api.UserInfoApi;
import com.bu.getactivecore.service.users.entity.RegistrationRequestDto;
import com.bu.getactivecore.service.users.entity.RegistrationResponseDto;
import com.bu.getactivecore.service.users.entity.TestResponseDto;
import com.bu.getactivecore.shared.exception.ApiException;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Entry point for all user related requests.
 */
@Slf4j
@RestController
@RequestMapping(value = "/v1")
public class UsersController {

    @Autowired
    private UserInfoApi m_userInfoApi;

    @GetMapping(path = "/test")
    public TestResponseDto test(HttpServletRequest request) {
        log.debug("Got request at /test");
        return new TestResponseDto(request.getSession().getId());
    }


    @PostMapping(path = "/register", consumes = "application/json")
    public RegistrationResponseDto registerUser(@RequestBody(required = true) RegistrationRequestDto registerUserDto) throws ApiException {
        log.debug("Got request at /register");
        return m_userInfoApi.registerUser(registerUserDto);
    }
}
