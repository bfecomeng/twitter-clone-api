package com.workintech.twitter.dto;

public record LoginResponse(
        String token,
        String tokenType,
        long expiresIn,
        UserResponse user
) {
}
