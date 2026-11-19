package com.workintech.twitter.service;

import com.workintech.twitter.dto.LikeResponse;
import com.workintech.twitter.entity.Tweet;
import com.workintech.twitter.entity.TweetLike;
import com.workintech.twitter.entity.User;
import com.workintech.twitter.exception.TwitterException;
import com.workintech.twitter.repository.TweetLikeRepository;
import com.workintech.twitter.repository.TweetRepository;
import com.workintech.twitter.repository.UserRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class LikeServiceImpl implements LikeService {

    private final TweetLikeRepository likeRepository;
    private final TweetRepository tweetRepository;
    private final UserRepository userRepository;

    public LikeServiceImpl(TweetLikeRepository likeRepository, TweetRepository tweetRepository,
                           UserRepository userRepository) {
        this.likeRepository = likeRepository;
        this.tweetRepository = tweetRepository;
        this.userRepository = userRepository;
    }

    @Override
    @Transactional
    public LikeResponse like(Long currentUserId, Long tweetId) {
        Tweet tweet = tweetRepository.findById(tweetId)
                .orElseThrow(() -> new TwitterException("Tweet bulunamadi: " + tweetId, HttpStatus.NOT_FOUND));
        // Hizli ve anlasilir kontrol. Ayni anda iki istek gelirse DB'deki
        // uk_like_user_tweet kisiti yakalar (GlobalExceptionHandler -> 409).
        if (likeRepository.existsByUserIdAndTweetId(currentUserId, tweetId)) {
            throw new TwitterException("Bu tweeti zaten begendiniz", HttpStatus.CONFLICT);
        }
        User user = userRepository.findById(currentUserId)
                .orElseThrow(() -> new TwitterException("Kullanici bulunamadi", HttpStatus.NOT_FOUND));

        TweetLike like = new TweetLike();
        like.setUser(user);
        like.setTweet(tweet);
        return LikeResponse.from(likeRepository.save(like));
    }

    @Override
    @Transactional
    public void dislike(Long currentUserId, Long tweetId) {
        // README: dislike = daha once atilmis like'i geri almak.
        TweetLike like = likeRepository.findByUserIdAndTweetId(currentUserId, tweetId)
                .orElseThrow(() -> new TwitterException("Bu tweeti begenmemissiniz", HttpStatus.NOT_FOUND));
        likeRepository.delete(like);
    }
}
