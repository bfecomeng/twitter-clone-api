package com.workintech.twitter.service;

import com.workintech.twitter.dto.RetweetResponse;

public interface RetweetService {

    RetweetResponse retweet(Long currentUserId, Long tweetId);

    void delete(Long currentUserId, Long retweetId);
}
