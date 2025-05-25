package com.bu.getactivecore.service.users;

import com.bu.getactivecore.config.JavaGmailMailConfig;
import com.bu.getactivecore.repository.UserRepository;
import com.bu.getactivecore.service.email.EmailVerificationService;
import com.bu.getactivecore.shared.ErrorCode;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

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
    void given_registered_user_and_same_username_used_for_registration_then_4xx_returned() throws Exception {
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
                    "email": "anothermail@bu.edu",
                    "username": "test",
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
}