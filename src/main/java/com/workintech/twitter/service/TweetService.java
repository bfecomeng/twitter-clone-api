package com.workintech.twitter.service;

import com.workintech.twitter.dto.TweetRequest;
import com.workintech.twitter.dto.TweetResponse;

import java.util.List;

public interface TweetService {

    TweetResponse create(Long currentUserId, TweetRequest request);

    List<TweetResponse> findByUserId(Long userId);

    TweetResponse findById(Long tweetId);

    TweetResponse update(Long currentUserId, Long tweetId, TweetRequest request);

    void delete(Long currentUserId, Long tweetId);
}
