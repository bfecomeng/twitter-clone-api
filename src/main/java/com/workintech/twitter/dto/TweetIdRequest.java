package com.workintech.twitter.dto;

import jakarta.validation.constraints.NotNull;

// like, dislike ve retweet isteklerinin ortak govdesi: {"tweetId": 5}
public record TweetIdRequest(
        @NotNull(message = "tweetId zorunlu") Long tweetId
) {
}
