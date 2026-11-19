package com.workintech.twitter.repository;

import com.workintech.twitter.entity.TweetLike;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface TweetLikeRepository extends JpaRepository<TweetLike, Long> {

    boolean existsByUserIdAndTweetId(Long userId, Long tweetId);

    Optional<TweetLike> findByUserIdAndTweetId(Long userId, Long tweetId);
}
