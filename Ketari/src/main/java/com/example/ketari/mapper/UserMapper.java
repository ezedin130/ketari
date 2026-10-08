package com.example.ketari.mapper;

import com.example.ketari.dto.user.UserResponse;
import com.example.ketari.model.User;

/**
 * Mapper for User entity and response DTO conversions.
 */
public final class UserMapper {

    private UserMapper() {
    }

    public static UserResponse toResponse(User user) {
        if (user == null) {
            return null;
        }
        return UserResponse.builder()
                .id(user.getId())
                .name(user.getName())
                .email(user.getEmail())
                .role(user.getRole())
                .isApproved(user.getIsApproved())
                .build();
    }
}
