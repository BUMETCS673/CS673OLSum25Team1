package com.bu.getactivecore.service.registration;

import com.bu.getactivecore.model.users.AccountState;
import com.bu.getactivecore.model.users.Users;
import com.bu.getactivecore.repository.UserRepository;
import com.bu.getactivecore.service.email.api.EmailApi;
import com.bu.getactivecore.service.jwt.api.JwtApi;
import com.bu.getactivecore.service.registration.api.RegistrationApi;
import com.bu.getactivecore.service.registration.entity.ConfirmRegistrationRequestDto;
import com.bu.getactivecore.service.registration.entity.RegistrationRequestDto;
import com.bu.getactivecore.service.registration.entity.RegistrationResponseDto;
import com.bu.getactivecore.service.registration.entity.RegistrationStatus;
import com.bu.getactivecore.service.users.entity.UserDto;
import com.bu.getactivecore.shared.ApiErrorPayload;
import com.bu.getactivecore.shared.exception.ApiException;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

import static com.bu.getactivecore.shared.Constants.PASSWORD_ENCODER_STRENGTH;
import static com.bu.getactivecore.shared.ErrorCode.EMAIL_USERNAME_TAKEN;
import static com.bu.getactivecore.shared.ErrorCode.TOKEN_EXPIRED;
import static com.bu.getactivecore.shared.ErrorCode.TOKEN_INVALID;
import static org.springframework.http.HttpStatus.BAD_REQUEST;

/**
 * Core logic for managing user related operations.
 */
@Slf4j
@Service
public class RegistrationService implements RegistrationApi {

    private static final Object VERIFICATION_LOCK = new Object();
    private static final Object REGISTRATION_LOCK = new Object();
    private final EmailApi m_emailApi;
    private final UserRepository m_userRepo;
    private final JwtApi m_jwtApi;
    private final BCryptPasswordEncoder m_passwordEncoder = new BCryptPasswordEncoder(PASSWORD_ENCODER_STRENGTH);

    /**
     * Constructor for UsersService.
     *
     * @param emailApi used for ending verification email
     * @param userRepo used for user related operations
     */
    public RegistrationService(EmailApi emailApi, UserRepository userRepo, JwtApi jwtApi) {
        m_emailApi = emailApi;
        m_userRepo = userRepo;
        m_jwtApi = jwtApi;
    }


    /**
     * Helper to build a descriptive debug message when a duplicate user is detected.
     *
     * @param existingUser The existing user found in database.
     * @param email        Email to check.
     * @param username     Username to check.
     * @return Debug message indicating which fields are taken.
     */
    private String buildDebugMessage(Users existingUser, String email, String username) {
        String debugMessage = "";
        if (existingUser.getEmail().equals(email) && existingUser.getUsername().equals(username)) {
            debugMessage = String.format("Email '%s' and username '%s' are already taken", email, username);
        } else if (existingUser.getEmail().equals(email)) {
            debugMessage = String.format("Email '%s' is already taken", email);
        } else if (existingUser.getUsername().equals(username)) {
            debugMessage = String.format("Username '%s' is already taken", username);
        }
        return debugMessage;
    }

    @Override
    @Transactional
    public RegistrationResponseDto registerUser(RegistrationRequestDto requestDto) throws ApiException {
        String email = requestDto.getEmail();
        String username = requestDto.getUsername();

        synchronized (REGISTRATION_LOCK) {
            Optional<Users> existingUser = m_userRepo.findByEmailOrUserName(email, username);
            if (existingUser.isPresent()) {
                String msg = String.format("Email '%s' or username '%s' is already taken", email, username);
                String debugMessage = buildDebugMessage(existingUser.get(), email, username);
                ApiErrorPayload error = ApiErrorPayload.builder().status(BAD_REQUEST).errorCode(EMAIL_USERNAME_TAKEN)
                        .message(msg)
                        .debugMessage(debugMessage)
                        .build();
                throw new ApiException(error);
            }
        }

        String encodedPassword = m_passwordEncoder.encode(requestDto.getPassword());
        Users user = UserDto.from(email, username, encodedPassword);
        m_userRepo.save(user);

        // TODO: implement the registration url logic
        m_emailApi.sendVerificationEmail(email, "test_registration_url");
        return RegistrationResponseDto.builder()
                .status(RegistrationStatus.SUCCESS)
                .build();
    }


    @Override
    @Transactional
    public RegistrationResponseDto confirmRegistration(ConfirmRegistrationRequestDto verificationDto) {
        String username = validToken(verificationDto);
        synchronized (VERIFICATION_LOCK) {
            Users user = m_userRepo.findByUsername(username).orElseThrow(() -> {
                ApiErrorPayload error = ApiErrorPayload.builder().status(BAD_REQUEST).errorCode(TOKEN_INVALID)
                        .message("Invalid registration token provided, unknown user " + username)
                        .build();
                return new ApiException(error);
            });

            if (user.getAccountState() != AccountState.UNVERIFIED) {
                log.debug("User {} is already verified, current state: {}", username, user.getAccountState());
            } else {
                user.setAccountState(AccountState.VERIFIED);
                m_userRepo.save(user);
                log.info("Successfully verified user's registration: {}", username);
            }
        }
        return RegistrationResponseDto.builder()
                .status(RegistrationStatus.SUCCESS)
                .build();
    }

    /**
     * Validates the provided token and extracts the username from it.
     *
     * @param verificationDto containing the token to validate.
     * @return the username extracted from the token.
     * @throws ApiException if the token is invalid or expired.
     */
    private String validToken(ConfirmRegistrationRequestDto verificationDto) {
        String username;
        try {
            m_jwtApi.validateToken(verificationDto.getToken());
            username = m_jwtApi.getUsername(verificationDto.getToken());
        } catch (ExpiredJwtException e) {
            ApiErrorPayload error = ApiErrorPayload.builder().status(BAD_REQUEST).errorCode(TOKEN_EXPIRED)
                    .message("Token has expired")
                    .debugMessage("Token expired at " + e.getClaims().getExpiration())
                    .build();
            throw new ApiException(error);
        } catch (JwtException e) {
            ApiErrorPayload error = ApiErrorPayload.builder().status(BAD_REQUEST).errorCode(TOKEN_INVALID)
                    .message("Invalid registration token provided")
                    .debugMessage("Provided token is invalid '" + verificationDto.getToken() + "'. Reason: " + e.getMessage())
                    .build();
            throw new ApiException(error);
        }
        return username;
    }
}
