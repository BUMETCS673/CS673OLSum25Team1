package com.bu.getactivecore.service.users;

import com.bu.getactivecore.model.users.Users;
import com.bu.getactivecore.model.users.UserPrincipal;
import com.bu.getactivecore.repository.UserRepository;
import com.bu.getactivecore.service.email.api.EmailApi;
import com.bu.getactivecore.service.jwt.api.JwtApi;
import com.bu.getactivecore.service.users.api.UserInfoApi;
import com.bu.getactivecore.service.users.entity.LoginRequestDto;
import com.bu.getactivecore.service.users.entity.LoginResponseDto;
import com.bu.getactivecore.service.users.entity.RegistrationRequestDto;
import com.bu.getactivecore.service.users.entity.RegistrationResponseDto;
import com.bu.getactivecore.service.users.entity.UserDto;
import com.bu.getactivecore.shared.ErrorCode;
import com.bu.getactivecore.shared.exception.ApiException;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

import static com.bu.getactivecore.shared.Constants.PASSWORD_ENCODER_STRENGTH;

/**
 * Core logic for managing user related operations.
 */
@Service
public class UsersService implements UserInfoApi {

    private final EmailApi m_emailApi;
    private final UserRepository m_userRepo;
    private final AuthenticationManager m_authManager;
    private final JwtApi m_jwtApi;

    private final BCryptPasswordEncoder m_passwordEncoder = new BCryptPasswordEncoder(PASSWORD_ENCODER_STRENGTH);

    /**
     * Constructor for UsersService.
     *
     * @param emailApi    used for ending verification email
     * @param userRepo    used for user related operations
     * @param authManager used for login operations
     */
    public UsersService(EmailApi emailApi, UserRepository userRepo, AuthenticationManager authManager, JwtApi jwtApi) {
        m_emailApi = emailApi;
        m_userRepo = userRepo;
        m_authManager = authManager;
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
    private static String buildDebugMessage(Users existingUser, String email, String username) {
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
    public RegistrationResponseDto registerUser(RegistrationRequestDto requestDto) throws ApiException {
        String email = requestDto.getEmail();
        String username = requestDto.getUsername();

        Optional<Users> existingUser = m_userRepo.findByEmailOrUserName(email, username);
        if (existingUser.isPresent()) {
            String msg = String.format("Email '%s' or username '%s' is already taken", email, username);
            String debugMessage = buildDebugMessage(existingUser.get(), email, username);
            throw new ApiException(HttpStatus.BAD_REQUEST, ErrorCode.EMAIL_USERNAME_TAKEN, msg, debugMessage);
        }

        Users user = UserDto.from(email, username);
        String encodedPassword = m_passwordEncoder.encode(requestDto.getPassword());
        user.setPassword(encodedPassword);
        m_userRepo.save(user);

        // TODO: implement the registration url logic
        m_emailApi.sendVerificationEmail(email, "test_registration_url");
        return RegistrationResponseDto.builder()
                .status(RegistrationResponseDto.RegistrationStatus.SUCCESS).build();
    }

    @Override
    public LoginResponseDto loginUser(LoginRequestDto requestDto) {
        // Given unauthenticated credentials, use the authentication manager to authenticate the user
        Authentication authentication = m_authManager.authenticate(
                new UsernamePasswordAuthenticationToken(requestDto.getUsername(), requestDto.getPassword()));

        // If authentication is successful, the user is logged in
        if (authentication.isAuthenticated()) {
            String token = m_jwtApi.generateToken(requestDto.getUsername());
            Users user = ((UserPrincipal) authentication.getPrincipal()).getUser();
            return new LoginResponseDto(token, user.getUsername(), user.getEmail());
        } else {
            throw new ApiException(HttpStatus.UNAUTHORIZED, ErrorCode.WRONG_CREDENTIALS, "Invalid credentials provided");
        }
    }
}
