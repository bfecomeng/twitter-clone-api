package com.workintech.twitter.dto;

import com.workintech.twitter.entity.Tweet;

import java.time.LocalDateTime;

public record TweetResponse(
        Long id,
        String content,
        Long userId,
        String username,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
    public static TweetResponse from(Tweet tweet) {
        return new TweetResponse(
                tweet.getId(),
                tweet.getContent(),
                tweet.getUser().getId(),
                tweet.getUser().getUsername(),
                tweet.getCreatedAt(),
                tweet.getUpdatedAt());
    }
}
