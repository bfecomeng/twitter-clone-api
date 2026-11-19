package com.workintech.twitter.service;

import com.workintech.twitter.dto.RetweetResponse;
import com.workintech.twitter.entity.Retweet;
import com.workintech.twitter.entity.Tweet;
import com.workintech.twitter.entity.User;
import com.workintech.twitter.exception.TwitterException;
import com.workintech.twitter.repository.RetweetRepository;
import com.workintech.twitter.repository.TweetRepository;
import com.workintech.twitter.repository.UserRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class RetweetServiceImpl implements RetweetService {

    private final RetweetRepository retweetRepository;
    private final TweetRepository tweetRepository;
    private final UserRepository userRepository;

    public RetweetServiceImpl(RetweetRepository retweetRepository, TweetRepository tweetRepository,
                              UserRepository userRepository) {
        this.retweetRepository = retweetRepository;
        this.tweetRepository = tweetRepository;
        this.userRepository = userRepository;
    }

    @Override
    @Transactional
    public RetweetResponse retweet(Long currentUserId, Long tweetId) {
        Tweet tweet = tweetRepository.findById(tweetId)
                .orElseThrow(() -> new TwitterException("Tweet bulunamadi: " + tweetId, HttpStatus.NOT_FOUND));
        // Twitter'daki gibi: kendi tweetini de retweet edebilirsin ama ayni tweeti iki kez edemezsin.
        if (retweetRepository.existsByUserIdAndTweetId(currentUserId, tweetId)) {
            throw new TwitterException("Bu tweeti zaten retweet ettiniz", HttpStatus.CONFLICT);
        }
        User user = userRepository.findById(currentUserId)
                .orElseThrow(() -> new TwitterException("Kullanici bulunamadi", HttpStatus.NOT_FOUND));

        Retweet retweet = new Retweet();
        retweet.setUser(user);
        retweet.setTweet(tweet);
        return RetweetResponse.from(retweetRepository.save(retweet));
    }

    @Override
    @Transactional
    public void delete(Long currentUserId, Long retweetId) {
        Retweet retweet = retweetRepository.findById(retweetId)
                .orElseThrow(() -> new TwitterException("Retweet bulunamadi: " + retweetId, HttpStatus.NOT_FOUND));
        if (!retweet.getUser().getId().equals(currentUserId)) {
            throw new TwitterException("Sadece kendi retweetinizi geri alabilirsiniz", HttpStatus.FORBIDDEN);
        }
        retweetRepository.delete(retweet);
    }
}
