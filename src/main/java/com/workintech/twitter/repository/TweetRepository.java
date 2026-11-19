package com.workintech.twitter.repository;

import com.workintech.twitter.entity.Tweet;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TweetRepository extends JpaRepository<Tweet, Long> {

    // "user.id" yolu: select * from tweets where user_id = ? order by created_at desc
    List<Tweet> findByUserIdOrderByCreatedAtDesc(Long userId);
}
