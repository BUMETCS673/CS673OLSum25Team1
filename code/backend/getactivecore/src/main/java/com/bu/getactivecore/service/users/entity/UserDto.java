package com.bu.getactivecore.service.users.entity;

import com.bu.getactivecore.model.Users;
import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class UserDto {
    private String email;
    private String username;
    private String password;

    public static UserDto of(Users user) {
        return new UserDto(user.getEmail(), user.getUsername(), user.getPassword());
    }

    public static Users from(String email, String username) {
        return Users.builder()
                .email(email)
                .username(username)
                .build();
    }
}

