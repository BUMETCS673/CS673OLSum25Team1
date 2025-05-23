package com.bu.getactivecore;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@Slf4j
@SpringBootApplication
public class GetActiveApplication {

    public static void main(String[] args) {
        SpringApplication.run(GetActiveApplication.class, args);
    }

    // @Autowired
    // private RegistrationApi m_registrationApi;

    // @EventListener(ApplicationReadyEvent.class)
    // public void onApplicationEvent() {
    //     log.info("GetActiveApplication is up and running!");
    //     log.info("Welcome to GetActive!");

    //     RegistrationRequestDto registrationRequestDto = new RegistrationRequestDto("adhillon@bu.edu", "arsh_username", "test_password");
        
    //     RegistrationResponseDto emailresult = m_registrationApi.registerUser(registrationRequestDto);
    //     log.info("Registration result: {}", emailresult);
    // }

}
