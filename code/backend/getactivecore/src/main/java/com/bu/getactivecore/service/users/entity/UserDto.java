package com.bu.getactivecore.service.users.entity;

import com.bu.getactivecore.model.users.Users;
import lombok.Builder;
import lombok.Data;

/**
 * DTO for exposing user data.
 * <p>For internal use only, not to be exposed in public APIs.
 */
@Builder
@Data
public class UserDto {

    /**
     * The email of the user.
     */
    private String email;

    /**
     * The username of the user.
     */
    private String username;

    /**
     * The password of the user.
     * Note: This field should not be exposed in public APIs for security reasons.
     */
    private String password;

    /**
     * Converts a Users entity to a UserDto.
     *
     * @param user the Users entity
     * @return the UserDto
     */
    public static UserDto of(Users user) {
        return new UserDto(user.getEmail(), user.getUsername(), user.getPassword());
    }

    /**
     * Converts a UserDto to a Users entity.
     *
     * @return the Users entity
     */
    public static Users from(String email, String username) {
        return Users.builder()
                .email(email)
                .username(username)
                .build();
    }
}

