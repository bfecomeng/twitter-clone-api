package com.workintech.twitter.repository;

import com.workintech.twitter.entity.Retweet;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RetweetRepository extends JpaRepository<Retweet, Long> {

    boolean existsByUserIdAndTweetId(Long userId, Long tweetId);
}
