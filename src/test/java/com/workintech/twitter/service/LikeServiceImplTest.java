package com.workintech.twitter.service;

import com.workintech.twitter.dto.LikeResponse;
import com.workintech.twitter.entity.Tweet;
import com.workintech.twitter.entity.TweetLike;
import com.workintech.twitter.entity.User;
import com.workintech.twitter.exception.TwitterException;
import com.workintech.twitter.repository.TweetLikeRepository;
import com.workintech.twitter.repository.TweetRepository;
import com.workintech.twitter.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class LikeServiceImplTest {

    @Mock
    private TweetLikeRepository likeRepository;
    @Mock
    private TweetRepository tweetRepository;
    @Mock
    private UserRepository userRepository;

    private LikeServiceImpl likeService;
    private Tweet tweet;
    private User ayse;

    @BeforeEach
    void setUp() {
        likeService = new LikeServiceImpl(likeRepository, tweetRepository, userRepository);
        tweet = new Tweet();
        tweet.setId(10L);
        ayse = new User();
        ayse.setId(2L);
    }

    @Test
    void like_basarili() {
        when(tweetRepository.findById(10L)).thenReturn(Optional.of(tweet));
        when(likeRepository.existsByUserIdAndTweetId(2L, 10L)).thenReturn(false);
        when(userRepository.findById(2L)).thenReturn(Optional.of(ayse));
        when(likeRepository.save(any(TweetLike.class))).thenAnswer(inv -> {
            TweetLike l = inv.getArgument(0);
            l.setId(1L);
            return l;
        });

        LikeResponse response = likeService.like(2L, 10L);

        assertEquals(10L, response.tweetId());
        assertEquals(2L, response.userId());
    }

    @Test
    void like_zatenBegenilmis_409FirlatirVeKaydetmez() {
        when(tweetRepository.findById(10L)).thenReturn(Optional.of(tweet));
        when(likeRepository.existsByUserIdAndTweetId(2L, 10L)).thenReturn(true);

        TwitterException ex = assertThrows(TwitterException.class, () -> likeService.like(2L, 10L));

        assertEquals(HttpStatus.CONFLICT, ex.getStatus());
        verify(likeRepository, never()).save(any());
    }

    @Test
    void dislike_likeVarsaSiler() {
        TweetLike like = new TweetLike();
        when(likeRepository.findByUserIdAndTweetId(2L, 10L)).thenReturn(Optional.of(like));

        likeService.dislike(2L, 10L);

        verify(likeRepository).delete(like);
    }

    @Test
    void dislike_likeYoksa_404Firlatir() {
        when(likeRepository.findByUserIdAndTweetId(2L, 10L)).thenReturn(Optional.empty());

        TwitterException ex = assertThrows(TwitterException.class, () -> likeService.dislike(2L, 10L));

        assertEquals(HttpStatus.NOT_FOUND, ex.getStatus());
    }
}
