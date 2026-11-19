package com.workintech.twitter.service;

import com.workintech.twitter.dto.LikeResponse;

public interface LikeService {

    LikeResponse like(Long currentUserId, Long tweetId);

    void dislike(Long currentUserId, Long tweetId);
}
