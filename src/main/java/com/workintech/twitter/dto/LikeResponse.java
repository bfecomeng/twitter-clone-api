package com.workintech.twitter.dto;

import com.workintech.twitter.entity.TweetLike;

import java.time.LocalDateTime;

public record LikeResponse(Long id, Long tweetId, Long userId, LocalDateTime createdAt) {

    public static LikeResponse from(TweetLike like) {
        return new LikeResponse(like.getId(), like.getTweet().getId(), like.getUser().getId(), like.getCreatedAt());
    }
}
