package com.bu.getactivecore.service.users;

import com.bu.getactivecore.model.Users;
import com.bu.getactivecore.repository.UserRepository;
import com.bu.getactivecore.service.email.api.EmailApi;
import com.bu.getactivecore.service.users.api.UserInfoApi;
import com.bu.getactivecore.service.users.entity.RegistrationRequestDto;
import com.bu.getactivecore.service.users.entity.RegistrationResponseDto;
import com.bu.getactivecore.service.users.entity.UserDto;
import com.bu.getactivecore.shared.ErrorCode;
import com.bu.getactivecore.shared.exception.ApiException;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import static com.bu.getactivecore.shared.Constants.PASSWORD_ENCODER_STRENGTH;

/**
 * Core logic for managing user related operations.
 */
@Service
public class UsersService implements UserInfoApi {

    private final EmailApi m_emailApi;
    private final UserRepository m_userRepo;

    private final BCryptPasswordEncoder m_passwordEncoder = new BCryptPasswordEncoder(PASSWORD_ENCODER_STRENGTH);

    /**
     * Constructor for UsersService.
     *
     * @param emailApi used for ending verification email
     * @param userRepo used for user related operations
     */
    public UsersService(EmailApi emailApi, UserRepository userRepo) {
        m_emailApi = emailApi;
        m_userRepo = userRepo;
    }


    @Override
    public RegistrationResponseDto registerUser(RegistrationRequestDto requestDto) throws ApiException {
        if (m_userRepo.existsByEmailAndUserName(requestDto.getEmail(), requestDto.getUsername())) {
            String msg = String.format("Email '%s' or username '%s' is already taken", requestDto.getEmail(), requestDto.getUsername());
            throw new ApiException(HttpStatus.BAD_REQUEST, ErrorCode.EMAIL_USERNAME_TAKEN, msg);
        }

        Users user = UserDto.from(requestDto.getEmail(), requestDto.getUsername());
        String encodedPassword = m_passwordEncoder.encode(requestDto.getPassword());
        user.setPassword(encodedPassword);
        m_userRepo.save(user);

        // TODO: implement the registration url logic
        m_emailApi.sendVerificationEmail(requestDto.getEmail(), "test_registration_url");
        return RegistrationResponseDto.builder()
                .status(RegistrationResponseDto.RegistrationStatus.SUCCESS).build();
    }

}
