package com.bu.getactivecore.service.users;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.time.LocalDateTime;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.Authentication;

import com.bu.getactivecore.model.users.UserPrincipal;
import com.bu.getactivecore.model.users.Users;
import com.bu.getactivecore.repository.UserRepository;
import com.bu.getactivecore.service.jwt.api.JwtApi;
import com.bu.getactivecore.service.users.api.UserInfoApi;
import com.bu.getactivecore.service.users.entity.UpdateAvatarRequestDto;
import com.bu.getactivecore.service.users.entity.UpdateAvatarResponseDto;
import com.bu.getactivecore.shared.ApiErrorPayload;
import com.bu.getactivecore.shared.ErrorCode;
import com.bu.getactivecore.shared.exception.ApiException;
import com.bu.getactivecore.shared.validation.AccountStateChecker;

@ExtendWith(MockitoExtension.class)
class UsersServiceTest {

    @Mock
    private AuthenticationManager authManager;

    @Mock
    private JwtApi jwtApi;

    @Mock
    private AccountStateChecker accountStateChecker;

    @Mock
    private UserRepository userRepository;

    private UsersService usersService;

    @BeforeEach
    void setUp() {
        usersService = new UsersService(authManager, jwtApi, accountStateChecker, userRepository);
    }

    @Test
    void updateAvatar_Success() {
        // Arrange
        String username = "testuser";
        String avatarData = "data:image/jpeg;base64," + "a".repeat(1000); // Small base64 string
        UpdateAvatarRequestDto requestDto = new UpdateAvatarRequestDto(avatarData);
        
        Users user = new Users();
        user.setUsername(username);
        user.setAvatar(null);
        user.setAvatarUpdatedAt(null);

        when(userRepository.findByUsername(username)).thenReturn(Optional.of(user));
        when(userRepository.save(any(Users.class))).thenAnswer(i -> i.getArguments()[0]);

        // Act
        UpdateAvatarResponseDto response = usersService.updateAvatar(username, requestDto);

        // Assert
        assertNotNull(response);
        assertEquals(avatarData, response.getAvatar());
        assertNotNull(response.getAvatarUpdatedAt());
        verify(userRepository).findByUsername(username);
        verify(userRepository).save(any(Users.class));
    }

    @Test
    void updateAvatar_ExceedsSizeLimit() {
        // Arrange
        String username = "testuser";
        // Create a base64 string that exceeds 3MB when decoded
        String largeBase64 = "data:image/jpeg;base64," + "a".repeat(10 * 1024 * 1024);
        UpdateAvatarRequestDto requestDto = new UpdateAvatarRequestDto(largeBase64);

        // Act & Assert
        ApiException exception = assertThrows(ApiException.class, () -> {
            usersService.updateAvatar(username, requestDto);
        });

        ApiErrorPayload error = exception.getError();
        assertEquals(ErrorCode.AVATAR_SIZE_EXCEEDS_LIMIT, error.getErrorCode());
        assertEquals("Avatar size exceeds 3MB limit", error.getMessage());
        verify(userRepository, never()).findByUsername(anyString());
        verify(userRepository, never()).save(any(Users.class));
    }

    @Test
    void updateAvatar_UserNotFound() {
        // Arrange
        String username = "nonexistentuser";
        String avatarData = "data:image/jpeg;base64," + "a".repeat(1000);
        UpdateAvatarRequestDto requestDto = new UpdateAvatarRequestDto(avatarData);

        when(userRepository.findByUsername(username)).thenReturn(Optional.empty());

        // Act & Assert
        ApiException exception = assertThrows(ApiException.class, () -> {
            usersService.updateAvatar(username, requestDto);
        });

        ApiErrorPayload error = exception.getError();
        assertEquals(ErrorCode.WRONG_CREDENTIALS, error.getErrorCode());
        assertEquals("User not found", error.getMessage());
        verify(userRepository).findByUsername(username);
        verify(userRepository, never()).save(any(Users.class));
    }

    @Test
    void updateAvatar_InvalidBase64Data() {
        // Arrange
        String username = "testuser";
        String invalidBase64 = "data:image/jpeg;base64,invalid-base64-data";
        UpdateAvatarRequestDto requestDto = new UpdateAvatarRequestDto(invalidBase64);

        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> {
            usersService.updateAvatar(username, requestDto);
        });

        verify(userRepository, never()).findByUsername(anyString());
        verify(userRepository, never()).save(any(Users.class));
    }
} 