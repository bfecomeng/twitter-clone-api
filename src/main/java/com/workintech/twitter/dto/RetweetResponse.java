package com.workintech.twitter.dto;

import com.workintech.twitter.entity.Retweet;

import java.time.LocalDateTime;

// Retweet'i kimin yaptigi + orijinal tweet'in kendisi.
public record RetweetResponse(
        Long id,
        Long userId,
        String username,
        TweetResponse originalTweet,
        LocalDateTime createdAt
) {
    public static RetweetResponse from(Retweet r) {
        return new RetweetResponse(r.getId(), r.getUser().getId(), r.getUser().getUsername(),
                TweetResponse.from(r.getTweet()), r.getCreatedAt());
    }
}
