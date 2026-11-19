package com.workintech.twitter.dto;

import com.workintech.twitter.entity.User;

import java.time.LocalDateTime;

// Client'a donen JSON. Dikkat: password alani YOK.
public record UserResponse(
        Long id,
        String username,
        String email,
        LocalDateTime createdAt
) {
    public static UserResponse from(User user) {
        return new UserResponse(user.getId(), user.getUsername(), user.getEmail(), user.getCreatedAt());
    }
}
