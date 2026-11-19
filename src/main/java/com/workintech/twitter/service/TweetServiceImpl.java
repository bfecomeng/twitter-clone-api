package com.workintech.twitter.service;

import com.workintech.twitter.dto.TweetRequest;
import com.workintech.twitter.dto.TweetResponse;
import com.workintech.twitter.entity.Tweet;
import com.workintech.twitter.entity.User;
import com.workintech.twitter.exception.TwitterException;
import com.workintech.twitter.repository.TweetRepository;
import com.workintech.twitter.repository.UserRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class TweetServiceImpl implements TweetService {

    private final TweetRepository tweetRepository;
    private final UserRepository userRepository;

    public TweetServiceImpl(TweetRepository tweetRepository, UserRepository userRepository) {
        this.tweetRepository = tweetRepository;
        this.userRepository = userRepository;
    }

    @Override
    @Transactional
    public TweetResponse create(Long currentUserId, TweetRequest request) {
        User user = findUserOrThrow(currentUserId);

        Tweet tweet = new Tweet();
        tweet.setContent(request.content());
        tweet.setUser(user);

        return TweetResponse.from(tweetRepository.save(tweet));
    }

    @Override
    @Transactional(readOnly = true)
    public List<TweetResponse> findByUserId(Long userId) {
        // Kullanici hic yoksa bos liste degil 404 donelim: "boyle biri yok" != "tweeti yok".
        if (!userRepository.existsById(userId)) {
            throw new TwitterException("Kullanici bulunamadi: " + userId, HttpStatus.NOT_FOUND);
        }
        return tweetRepository.findByUserIdOrderByCreatedAtDesc(userId).stream()
                .map(TweetResponse::from)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public TweetResponse findById(Long tweetId) {
        return TweetResponse.from(findTweetOrThrow(tweetId));
    }

    @Override
    @Transactional
    public TweetResponse update(Long currentUserId, Long tweetId, TweetRequest request) {
        Tweet tweet = findTweetOrThrow(tweetId);
        checkOwner(tweet, currentUserId, "Sadece kendi tweetinizi duzenleyebilirsiniz");

        tweet.setContent(request.content());
        // Transaction icinde yuklenen entity'deki degisiklik commit'te otomatik UPDATE olur
        // (dirty checking). saveAndFlush'i, @PreUpdate'in updatedAt'i HEMEN doldurmasi
        // ve cevapta gorunmesi icin kullaniyoruz.
        return TweetResponse.from(tweetRepository.saveAndFlush(tweet));
    }

    @Override
    @Transactional
    public void delete(Long currentUserId, Long tweetId) {
        Tweet tweet = findTweetOrThrow(tweetId);
        checkOwner(tweet, currentUserId, "Sadece kendi tweetinizi silebilirsiniz");
        // Yorumlar, like'lar, retweet'ler FK'deki ON DELETE CASCADE ile DB tarafinda silinir.
        tweetRepository.delete(tweet);
    }

    // --- yardimci metodlar ---

    private User findUserOrThrow(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new TwitterException("Kullanici bulunamadi: " + userId, HttpStatus.NOT_FOUND));
    }

    private Tweet findTweetOrThrow(Long tweetId) {
        return tweetRepository.findById(tweetId)
                .orElseThrow(() -> new TwitterException("Tweet bulunamadi: " + tweetId, HttpStatus.NOT_FOUND));
    }

    // 401 = "kim oldugunu bilmiyorum", 403 = "kim oldugunu biliyorum ama bu senin isin degil".
    private void checkOwner(Tweet tweet, Long currentUserId, String message) {
        if (!tweet.getUser().getId().equals(currentUserId)) {
            throw new TwitterException(message, HttpStatus.FORBIDDEN);
        }
    }
}
