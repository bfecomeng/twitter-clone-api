package com.workintech.twitter.service;

import com.workintech.twitter.dto.TweetRequest;
import com.workintech.twitter.dto.TweetResponse;
import com.workintech.twitter.entity.Tweet;
import com.workintech.twitter.entity.User;
import com.workintech.twitter.exception.TwitterException;
import com.workintech.twitter.repository.TweetRepository;
import com.workintech.twitter.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TweetServiceImplTest {

    @Mock
    private TweetRepository tweetRepository;
    @Mock
    private UserRepository userRepository;

    private TweetServiceImpl tweetService;

    private User brusk;
    private User ayse;

    @BeforeEach
    void setUp() {
        tweetService = new TweetServiceImpl(tweetRepository, userRepository);
        brusk = user(1L, "brusk");
        ayse = user(2L, "ayse");
    }

    @Test
    void create_tweetiTokenSahibineBaglar() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(brusk));
        when(tweetRepository.save(any(Tweet.class))).thenAnswer(inv -> {
            Tweet t = inv.getArgument(0);
            t.setId(10L);
            return t;
        });

        TweetResponse response = tweetService.create(1L, new TweetRequest("Merhaba"));

        assertEquals(10L, response.id());
        assertEquals("Merhaba", response.content());
        assertEquals(1L, response.userId());
    }

    @Test
    void findById_tweetYok_404Firlatir() {
        when(tweetRepository.findById(99L)).thenReturn(Optional.empty());

        TwitterException ex = assertThrows(TwitterException.class, () -> tweetService.findById(99L));

        assertEquals(HttpStatus.NOT_FOUND, ex.getStatus());
    }

    @Test
    void findByUserId_kullaniciYok_404Firlatir() {
        when(userRepository.existsById(999L)).thenReturn(false);

        TwitterException ex = assertThrows(TwitterException.class, () -> tweetService.findByUserId(999L));

        assertEquals(HttpStatus.NOT_FOUND, ex.getStatus());
        verify(tweetRepository, never()).findByUserIdOrderByCreatedAtDesc(any());
    }

    @Test
    void findByUserId_tweetleriDoner() {
        when(userRepository.existsById(1L)).thenReturn(true);
        when(tweetRepository.findByUserIdOrderByCreatedAtDesc(1L))
                .thenReturn(List.of(tweet(10L, brusk), tweet(11L, brusk)));

        List<TweetResponse> result = tweetService.findByUserId(1L);

        assertEquals(2, result.size());
    }

    @Test
    void update_baskasininTweeti_403Firlatir() {
        when(tweetRepository.findById(10L)).thenReturn(Optional.of(tweet(10L, brusk)));

        TwitterException ex = assertThrows(TwitterException.class,
                () -> tweetService.update(ayse.getId(), 10L, new TweetRequest("hack")));

        assertEquals(HttpStatus.FORBIDDEN, ex.getStatus());
        verify(tweetRepository, never()).saveAndFlush(any());
    }

    @Test
    void delete_sahibiSilebilir() {
        Tweet tweet = tweet(10L, brusk);
        when(tweetRepository.findById(10L)).thenReturn(Optional.of(tweet));

        tweetService.delete(brusk.getId(), 10L);

        verify(tweetRepository).delete(tweet);
    }

    @Test
    void delete_baskasininTweeti_403FirlatirVeSilmez() {
        when(tweetRepository.findById(10L)).thenReturn(Optional.of(tweet(10L, brusk)));

        TwitterException ex = assertThrows(TwitterException.class, () -> tweetService.delete(ayse.getId(), 10L));

        assertEquals(HttpStatus.FORBIDDEN, ex.getStatus());
        verify(tweetRepository, never()).delete(any());
    }

    // --- test verisi yardimcilari ---

    private static User user(Long id, String username) {
        User u = new User();
        u.setId(id);
        u.setUsername(username);
        return u;
    }

    private static Tweet tweet(Long id, User owner) {
        Tweet t = new Tweet();
        t.setId(id);
        t.setContent("icerik");
        t.setUser(owner);
        return t;
    }
}
