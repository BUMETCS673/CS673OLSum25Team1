package com.bu.getactivecore.service.registration;

import static com.bu.getactivecore.service.jwt.api.JwtApi.TOKEN_CLAIM_TYPE_KEY;
import static com.bu.getactivecore.service.jwt.api.JwtApi.TokenClaimType;
import static com.bu.getactivecore.shared.Constants.PASSWORD_ENCODER_STRENGTH;
import static com.bu.getactivecore.shared.ErrorCode.EMAIL_USERNAME_TAKEN;
import static com.bu.getactivecore.shared.ErrorCode.TOKEN_EXPIRED;
import static com.bu.getactivecore.shared.ErrorCode.TOKEN_INVALID;
import static org.springframework.http.HttpStatus.BAD_REQUEST;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.bu.getactivecore.model.users.AccountState;
import com.bu.getactivecore.model.users.Users;
import com.bu.getactivecore.repository.UserRepository;
import com.bu.getactivecore.service.email.api.EmailApi;
import com.bu.getactivecore.service.jwt.api.JwtApi;
import com.bu.getactivecore.service.registration.api.RegistrationApi;
import com.bu.getactivecore.service.registration.entity.ConfirmationRequestDto;
import com.bu.getactivecore.service.registration.entity.ConfirmationResendRequestDto;
import com.bu.getactivecore.service.registration.entity.ConfirmationResponseDto;
import com.bu.getactivecore.service.registration.entity.RegistrationRequestDto;
import com.bu.getactivecore.service.registration.entity.RegistrationResponseDto;
import com.bu.getactivecore.service.registration.entity.RegistrationStatus;
import com.bu.getactivecore.service.users.entity.UserDto;
import com.bu.getactivecore.shared.ApiErrorPayload;
import com.bu.getactivecore.shared.exception.ApiException;

import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import lombok.extern.slf4j.Slf4j;

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

	@Override
	@Transactional
	public RegistrationResponseDto registerUser(RegistrationRequestDto requestDto) throws ApiException {
		String email = requestDto.email();
		String username = requestDto.username();

		synchronized (REGISTRATION_LOCK) {
			boolean emailExists = m_userRepo.findByEmail(email).isPresent();
			boolean usernameExists = m_userRepo.findByUsername(username).isPresent();
			String debugMessage = null;
			if (emailExists && usernameExists) {
				debugMessage = String.format("Email '%s' and username '%s' are already taken", email, username);
			} else if (emailExists) {
				debugMessage = String.format("Email '%s' is already taken", email);
			} else if (usernameExists) {
				debugMessage = String.format("Username '%s' is already taken", username);
			}
			if (debugMessage != null) {
				ApiErrorPayload error = ApiErrorPayload.builder().status(BAD_REQUEST).errorCode(EMAIL_USERNAME_TAKEN)
						.message("Email or username already taken").debugMessage(debugMessage).build();
				throw new ApiException(error);
			}
		}

		String encodedPassword = m_passwordEncoder.encode(requestDto.password());
		Users user = UserDto.from(email, username, encodedPassword);
		m_userRepo.save(user);

		String registrationToken = m_jwtApi.generateToken(username, TokenClaimType.REGISTRATION_CONFIRMATION);
		m_emailApi.sendVerificationEmail(email, registrationToken);
		return RegistrationResponseDto.builder().token(registrationToken).build();
	}

	@Override
	@Transactional
	public ConfirmationResponseDto confirmRegistration(ConfirmationRequestDto verificationDto) {
		String username = validateConfirmationToken(verificationDto);
		synchronized (VERIFICATION_LOCK) {
			Users user = m_userRepo.findByUsername(username).orElseThrow(() -> {
				ApiErrorPayload error = ApiErrorPayload.builder().status(BAD_REQUEST).errorCode(TOKEN_INVALID)
						.message("Invalid registration token provided, unknown '" + username + "' username").build();
				return new ApiException(error);
			});

			switch (user.getAccountState()) {
			case VERIFIED ->
				log.debug("User '{}' is already verified, current state: {}", username, user.getAccountState());
			case UNVERIFIED -> {
				user.setAccountState(AccountState.VERIFIED);
				m_userRepo.save(user);
				log.info("Successfully confirmed registration for user '{}'", username);
			}
			}
		}
		return ConfirmationResponseDto.builder().status(RegistrationStatus.SUCCESS).build();
	}

	/**
	 * Validates the provided token and extracts the username from it.
	 *
	 * @param confirmRegistrationDto containing the token to validate.
	 * @return the username extracted from the token.
	 * @throws ApiException if the token is invalid or expired.
	 */
	private String validateConfirmationToken(ConfirmationRequestDto confirmRegistrationDto) {
		String username;
		try {
			m_jwtApi.validateToken(confirmRegistrationDto.token());
			String claim = m_jwtApi.getClaim(confirmRegistrationDto.token(), TOKEN_CLAIM_TYPE_KEY);
			if (!TokenClaimType.REGISTRATION_CONFIRMATION.name().equals(claim)) {
				ApiErrorPayload error = ApiErrorPayload.builder().status(BAD_REQUEST).errorCode(TOKEN_INVALID)
						.message("Invalid registration confirmation token provided")
						.debugMessage("Only tokens with type '" + TokenClaimType.REGISTRATION_CONFIRMATION.name()
								+ "' are allowed, but got '" + claim + "'")
						.build();
				throw new ApiException(error);
			}
			username = m_jwtApi.getUsername(confirmRegistrationDto.token());
		} catch (ExpiredJwtException e) {
			ApiErrorPayload error = ApiErrorPayload.builder().status(BAD_REQUEST).errorCode(TOKEN_EXPIRED)
					.message("Token has expired").debugMessage("Token expired at " + e.getClaims().getExpiration())
					.build();
			throw new ApiException(error);
		} catch (JwtException e) {
			ApiErrorPayload error = ApiErrorPayload.builder().status(BAD_REQUEST).errorCode(TOKEN_INVALID)
					.message("Invalid registration token provided").debugMessage("Provided token is invalid '"
							+ confirmRegistrationDto.token() + "'. Reason: " + e.getMessage())
					.build();
			throw new ApiException(error);
		}
		return username;
	}

	@Override
	public void resendConfirmation(ConfirmationResendRequestDto resendRequestDto) {
		m_userRepo.findByEmailAndUsername(resendRequestDto.email(), resendRequestDto.username())
				.ifPresentOrElse(user -> {
					if (user.getAccountState() == AccountState.UNVERIFIED) {
						String registrationToken = m_jwtApi.generateToken(user.getUsername(),
								TokenClaimType.REGISTRATION_CONFIRMATION);
						m_emailApi.sendVerificationEmail(user.getEmail(), registrationToken);
						log.info("Resent confirmation email to user '{}'", user.getUsername());
					} else {
						log.warn("Not resending confirmation email to user '{}', current state: {}", user.getUsername(),
								user.getAccountState());
					}
				}, () -> log.debug("Cannot resend confirmation email, no user found with email/username '{}'/'{}'",
						resendRequestDto.email(), resendRequestDto.username()));
	}
}
