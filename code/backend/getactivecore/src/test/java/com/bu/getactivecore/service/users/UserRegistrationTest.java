package com.bu.getactivecore.service.users;

import com.bu.getactivecore.config.JavaGmailMailConfig;
import com.bu.getactivecore.model.users.AccountState;
import com.bu.getactivecore.model.users.Users;
import com.bu.getactivecore.repository.UserRepository;
import com.bu.getactivecore.service.email.EmailVerificationService;
import com.bu.getactivecore.service.jwt.api.JwtApi;
import com.bu.getactivecore.service.registration.entity.ConfirmRegistrationRequestDto;
import com.bu.getactivecore.service.registration.entity.RegistrationRequestDto;
import com.bu.getactivecore.shared.ErrorCode;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Optional;

import static com.bu.getactivecore.service.jwt.api.JwtApi.TokenClaimType;
import static com.bu.getactivecore.util.RestUtil.confirmRegistration;
import static com.bu.getactivecore.util.RestUtil.register;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class UserRegistrationTest {

    @MockitoBean
    private EmailVerificationService emailVerificationService;

    @MockitoBean
    private JavaGmailMailConfig javaGmailMailConfig;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private JwtApi jwtApi;

    @AfterEach
    void setUp() {
        userRepository.deleteAll();
    }

    @Test
    void given_empty_request_body_then_4xx_returned() throws Exception {
        mockMvc.perform(post("/v1/register").contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().is4xxClientError())
                .andExpect(jsonPath("$.errors").exists());

        String invalidRequestJson = """
                {
                    "unknownkey": ""
                }
                """;

        mockMvc.perform(post("/v1/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(invalidRequestJson))
                .andExpect(status().is4xxClientError())
                .andExpect(jsonPath("$.errors").exists());
    }

    @Test
    void given_invalid_content_type_then_4xx_returned() throws Exception {
        mockMvc.perform(post("/v1/register"))
                .andDo(print())
                .andExpect(status().is4xxClientError())
                .andExpect(jsonPath("$.errors").exists());
    }

    @Test
    void given_non_bu_email_then_4xx_returned() throws Exception {
        String invalidRequestJson = """
                {
                    "email": "1234@gmail.com",
                    "username": "testuser",
                    "password": "testpassword"
                }
                """;

        mockMvc.perform(post("/v1/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(invalidRequestJson))
                .andExpect(status().is4xxClientError())
                .andDo(print())
                .andExpect(jsonPath("$.errors").exists())
                .andExpect(jsonPath("$.errors.validationErrors").exists())
                .andExpect(jsonPath("$.errors.validationErrors.email").exists());
    }

    @Test
    void given_invalid_email_then_4xx_returned() throws Exception {
        String invalidRequestJson = """
                {
                    "email": "",
                    "username": "testuser",
                    "password": "testpassword"
                }
                """;

        mockMvc.perform(post("/v1/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(invalidRequestJson))
                .andExpect(status().is4xxClientError())
                .andDo(print())
                .andExpect(jsonPath("$.errors").exists())
                .andExpect(jsonPath("$.errors.validationErrors").exists())
                .andExpect(jsonPath("$.errors.validationErrors.email").exists());
    }

    @Test
    void given_empty_username_then_4xx_returned() throws Exception {
        String invalidRequestJson = """
                {
                    "email": "123@bu.edu",
                    "username": "",
                    "password": "testpassword"
                }
                """;

        mockMvc.perform(post("/v1/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(invalidRequestJson))
                .andExpect(status().is4xxClientError())
                .andDo(print())
                .andExpect(jsonPath("$.errors").exists())
                .andExpect(jsonPath("$.errors.validationErrors").exists())
                .andExpect(jsonPath("$.errors.validationErrors.username").exists())
                .andExpect(jsonPath("$.errors.validationErrors.email").doesNotExist())
                .andExpect(jsonPath("$.errors.validationErrors.password").doesNotExist());
    }

    @Test
    void given_too_long_username_then_4xx_returned() throws Exception {
        String invalidRequestJson = """
                {
                    "email": "123@bu.edu",
                    "username": "this_is_really_really_long_username_that_exceeds_the_maximum_length",
                    "password": "testpassword"
                }
                """;

        mockMvc.perform(post("/v1/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(invalidRequestJson))
                .andExpect(status().is4xxClientError())
                .andDo(print())
                .andExpect(jsonPath("$.errors").exists())
                .andExpect(jsonPath("$.errors.validationErrors").exists())
                .andExpect(jsonPath("$.errors.validationErrors.username").exists())
                .andExpect(jsonPath("$.errors.validationErrors.email").doesNotExist())
                .andExpect(jsonPath("$.errors.validationErrors.password").doesNotExist());
    }


    @Test
    void given_empty_password_then_4xx_returned() throws Exception {
        String invalidRequestJson = """
                {
                    "email": "123@bu.edu",
                    "username": "testusername",
                    "password": ""
                }
                """;

        mockMvc.perform(post("/v1/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(invalidRequestJson))
                .andExpect(status().is4xxClientError())
                .andDo(print())
                .andExpect(jsonPath("$.errors").exists())
                .andExpect(jsonPath("$.errors.validationErrors").exists())
                .andExpect(jsonPath("$.errors.validationErrors.username").doesNotExist())
                .andExpect(jsonPath("$.errors.validationErrors.email").doesNotExist())
                .andExpect(jsonPath("$.errors.validationErrors.password").exists());
    }

    @Test
    void given_too_long_password_then_4xx_returned() throws Exception {
        String invalidRequestJson = """
                {
                    "email": "123@bu.edu",
                    "username": "testusername",
                    "password": "this_is_really_really_long_password_that_exceeds_the_maximum_length"
                }
                """;

        mockMvc.perform(post("/v1/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(invalidRequestJson))
                .andExpect(status().is4xxClientError())
                .andDo(print())
                .andExpect(jsonPath("$.errors").exists())
                .andExpect(jsonPath("$.errors.validationErrors").exists())
                .andExpect(jsonPath("$.errors.validationErrors.username").doesNotExist())
                .andExpect(jsonPath("$.errors.validationErrors.email").doesNotExist())
                .andExpect(jsonPath("$.errors.validationErrors.password").exists());
    }

    @Test
    void given_multiple_validation_errors_then_multiple_validation_errors_and_4xx_are_returned() throws Exception {
        String invalidRequestJson = """
                {
                    "email": "",
                    "username": "",
                    "password": ""
                }
                """;

        mockMvc.perform(post("/v1/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(invalidRequestJson))
                .andExpect(status().is4xxClientError())
                .andDo(print())
                .andExpect(jsonPath("$.errors").exists())
                .andExpect(jsonPath("$.errors.validationErrors").exists())
                .andExpect(jsonPath("$.errors.validationErrors.username").isArray())
                .andExpect(jsonPath("$.errors.validationErrors.email").isArray())
                .andExpect(jsonPath("$.errors.validationErrors.password").isArray());
    }


    @Test
    void given_registered_user_and_same_credentials_used_for_registration_then_4xx_returned() throws Exception {
        String validUserReq1 = """
                {
                    "email": "1234@bu.edu",
                    "username": "test",
                    "password": "test"
                }
                """;

        mockMvc.perform(post("/v1/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(validUserReq1))
                .andExpect(status().is2xxSuccessful())
                .andDo(print())
                .andExpect(jsonPath("$.errors").doesNotExist());

        mockMvc.perform(post("/v1/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(validUserReq1))
                .andExpect(status().is4xxClientError())
                .andDo(print())
                .andExpect(jsonPath("$.errors").exists())
                .andExpect(jsonPath("$.errors.errorCode").exists())
                .andExpect(jsonPath("$.errors.errorCode").value(ErrorCode.EMAIL_USERNAME_TAKEN.getCode()));
    }

    @Test
    void given_registered_user_and_same_email_used_for_registration_then_4xx_returned() throws Exception {
        String validUserReq1 = """
                {
                    "email": "1234@bu.edu",
                    "username": "test",
                    "password": "test"
                }
                """;

        mockMvc.perform(post("/v1/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(validUserReq1))
                .andExpect(status().is2xxSuccessful())
                .andDo(print())
                .andExpect(jsonPath("$.errors").doesNotExist());

        String userReq2 = """
                {
                    "email": "1234@bu.edu",
                    "username": "test2",
                    "password": "test"
                }
                """;
        mockMvc.perform(post("/v1/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(userReq2))
                .andExpect(status().is4xxClientError())
                .andDo(print())
                .andExpect(jsonPath("$.errors").exists())
                .andExpect(jsonPath("$.errors.errorCode").exists())
                .andExpect(jsonPath("$.errors.errorCode").value(ErrorCode.EMAIL_USERNAME_TAKEN.getCode()));
    }

    @Test
    void verify_new_registered_user_is_assigned_UNVERIFIED_state() throws Exception {
        String username = "test";
        String email = "1234@bu.edu";
        String password = "test";
        RegistrationRequestDto validUserReq1 = new RegistrationRequestDto(email, username, password);

        register(mockMvc, validUserReq1)
                .andExpect(status().is2xxSuccessful())
                .andDo(print())
                .andExpect(jsonPath("$.errors").doesNotExist());
        Optional<Users> testUser = userRepository.findByUsername(username);
        Assertions.assertTrue(testUser.isPresent());
        Assertions.assertEquals(AccountState.UNVERIFIED.name(), testUser.get().getAccountState().name());
    }

    @Test
    void given_registered_user_and_same_username_used_for_registration_then_4xx_returned() throws Exception {
        RegistrationRequestDto validUserReq1 = new RegistrationRequestDto("1234@bu.edu", "test", "test");

        register(mockMvc, validUserReq1)
                .andExpect(status().is2xxSuccessful())
                .andDo(print())
                .andExpect(jsonPath("$.errors").doesNotExist());

        RegistrationRequestDto userReq2 = new RegistrationRequestDto("anothermail@bu.edu", "test", "test");
        register(mockMvc, userReq2)
                .andExpect(status().is4xxClientError())
                .andDo(print())
                .andExpect(jsonPath("$.errors").exists())
                .andExpect(jsonPath("$.errors.errorCode").exists())
                .andExpect(jsonPath("$.errors.errorCode").value(ErrorCode.EMAIL_USERNAME_TAKEN.getCode()));
    }

    @Test
    void given_empty_confirmation_token_then_4xx_returned() throws Exception {
        RegistrationRequestDto validUserReq1 = new RegistrationRequestDto("1234@bu.edu", "test", "test");

        register(mockMvc, validUserReq1)
                .andExpect(status().is2xxSuccessful())
                .andDo(print())
                .andExpect(jsonPath("$.errors").doesNotExist());

        ConfirmRegistrationRequestDto confirmReq = new ConfirmRegistrationRequestDto("");
        confirmRegistration(mockMvc, confirmReq)
                .andExpect(status().is4xxClientError())
                .andDo(print())
                .andExpect(jsonPath("$.data").doesNotExist())
                .andExpect(jsonPath("$.errors").exists())
                .andExpect(jsonPath("$.errors.errorCode").exists())
                .andExpect(jsonPath("$.errors.errorCode").value(ErrorCode.DATA_STRUCTURE_INVALID.getCode()));
    }

    @Test
    void given_invalid_confirmation_token_then_4xx_returned() throws Exception {
        RegistrationRequestDto validUserReq1 = new RegistrationRequestDto("1234@bu.edu", "test", "test");

        register(mockMvc, validUserReq1)
                .andExpect(status().is2xxSuccessful())
                .andDo(print())
                .andExpect(jsonPath("$.errors").doesNotExist());

        ConfirmRegistrationRequestDto confirmReq = new ConfirmRegistrationRequestDto("invalid.token");
        confirmRegistration(mockMvc, confirmReq)
                .andExpect(status().is4xxClientError())
                .andDo(print())
                .andExpect(jsonPath("$.data").doesNotExist())
                .andExpect(jsonPath("$.errors").exists())
                .andExpect(jsonPath("$.errors.errorCode").exists())
                .andExpect(jsonPath("$.errors.errorCode").value(ErrorCode.TOKEN_INVALID.getCode()));
    }

    @Test
    void given_unknown_confirmation_user_token_then_4xx_returned() throws Exception {
        RegistrationRequestDto validUserReq1 = new RegistrationRequestDto("1234@bu.edu", "test", "test");

        register(mockMvc, validUserReq1)
                .andExpect(status().is2xxSuccessful())
                .andDo(print())
                .andExpect(jsonPath("$.errors").doesNotExist());

        String token = jwtApi.generateToken("not_test_user");
        ConfirmRegistrationRequestDto confirmReq = new ConfirmRegistrationRequestDto(token);
        confirmRegistration(mockMvc, confirmReq)
                .andExpect(status().is4xxClientError())
                .andDo(print())
                .andExpect(jsonPath("$.data").doesNotExist())
                .andExpect(jsonPath("$.errors").exists())
                .andExpect(jsonPath("$.errors.errorCode").exists())
                .andExpect(jsonPath("$.errors.errorCode").value(ErrorCode.TOKEN_INVALID.getCode()));

        Optional<Users> registeredUser = userRepository.findByUsername("test");
        Assertions.assertTrue(registeredUser.isPresent());
        Assertions.assertEquals(AccountState.UNVERIFIED.name(), registeredUser.get().getAccountState().name());
    }


    @Test
    void given_non_confirmation_token_then_user_remains_UNVERIFIED() throws Exception {
        RegistrationRequestDto validUserReq1 = new RegistrationRequestDto("1234@bu.edu", "test", "test");

        register(mockMvc, validUserReq1)
                .andExpect(status().is2xxSuccessful())
                .andDo(print())
                .andExpect(jsonPath("$.errors").doesNotExist());

        String token = jwtApi.generateToken("test");
        ConfirmRegistrationRequestDto confirmReq = new ConfirmRegistrationRequestDto(token);
        confirmRegistration(mockMvc, confirmReq)
                .andExpect(status().is4xxClientError())
                .andDo(print())
                .andExpect(jsonPath("$.data").doesNotExist())
                .andExpect(jsonPath("$.errors").exists())
                .andExpect(jsonPath("$.errors.errorCode").exists())
                .andExpect(jsonPath("$.errors.errorCode").value(ErrorCode.TOKEN_INVALID.getCode()));

        Optional<Users> registeredUser = userRepository.findByUsername("test");
        Assertions.assertTrue(registeredUser.isPresent());
        Assertions.assertEquals(AccountState.UNVERIFIED.name(), registeredUser.get().getAccountState().name());
    }

    @Test
    void given_confirmation_token_then_user_is_VERIFIED() throws Exception {
        RegistrationRequestDto validUserReq1 = new RegistrationRequestDto("1234@bu.edu", "test", "test");

        register(mockMvc, validUserReq1)
                .andExpect(status().is2xxSuccessful())
                .andDo(print())
                .andExpect(jsonPath("$.errors").doesNotExist());

        String token = jwtApi.generateToken("test", TokenClaimType.REGISTRATION_CONFIRMATION);
        ConfirmRegistrationRequestDto confirmReq = new ConfirmRegistrationRequestDto(token);
        confirmRegistration(mockMvc, confirmReq)
                .andExpect(status().is2xxSuccessful())
                .andDo(print())
                .andExpect(jsonPath("$.errors").doesNotExist())
                .andExpect(jsonPath("$.data").exists())
                .andExpect(jsonPath("$.data.status").value("SUCCESS"));

        Optional<Users> registeredUser = userRepository.findByUsername("test");
        Assertions.assertTrue(registeredUser.isPresent());
        Assertions.assertEquals(AccountState.VERIFIED.name(), registeredUser.get().getAccountState().name());
    }


    @Test
    void verify_register_confirmation_is_idempotent() throws Exception {
        String username = "test";
        String email = "1234@bu.edu";
        String password = "test";
        RegistrationRequestDto validUserReq1 = new RegistrationRequestDto(email, username, password);

        register(mockMvc, validUserReq1)
                .andExpect(status().is2xxSuccessful())
                .andDo(print())
                .andExpect(jsonPath("$.errors").doesNotExist());

        String token = jwtApi.generateToken(username, TokenClaimType.REGISTRATION_CONFIRMATION);
        ConfirmRegistrationRequestDto confirmReq = new ConfirmRegistrationRequestDto(token);
        confirmRegistration(mockMvc, confirmReq)
                .andExpect(status().is2xxSuccessful())
                .andDo(print())
                .andExpect(jsonPath("$.errors").doesNotExist())
                .andExpect(jsonPath("$.data").exists())
                .andExpect(jsonPath("$.data.status").exists())
                .andExpect(jsonPath("$.data.status").value("SUCCESS"));

        Optional<Users> testUser = userRepository.findByUsername(username);
        Assertions.assertTrue(testUser.isPresent());
        Assertions.assertEquals(AccountState.VERIFIED.name(), testUser.get().getAccountState().name());


        // Confirming again should not change the state
        confirmRegistration(mockMvc, confirmReq)
                .andExpect(status().is2xxSuccessful())
                .andDo(print())
                .andExpect(jsonPath("$.errors").doesNotExist())
                .andExpect(jsonPath("$.data").exists())
                .andExpect(jsonPath("$.data.status").exists())
                .andExpect(jsonPath("$.data.status").value("SUCCESS"));

        testUser = userRepository.findByUsername(username);
        Assertions.assertTrue(testUser.isPresent());
        Assertions.assertEquals(AccountState.VERIFIED.name(), testUser.get().getAccountState().name());
    }
}