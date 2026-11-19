package com.workintech.twitter.service;

import com.workintech.twitter.dto.CommentUpdateRequest;
import com.workintech.twitter.entity.Comment;
import com.workintech.twitter.entity.Tweet;
import com.workintech.twitter.entity.User;
import com.workintech.twitter.exception.TwitterException;
import com.workintech.twitter.repository.CommentRepository;
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
class CommentServiceImplTest {

    @Mock
    private CommentRepository commentRepository;
    @Mock
    private TweetRepository tweetRepository;
    @Mock
    private UserRepository userRepository;

    private CommentServiceImpl commentService;

    // Senaryo: Brusk'in tweetine Ayse yorum yazmis. Mehmet ise alakasiz biri.
    private final User brusk = user(1L);
    private final User ayse = user(2L);
    private final User mehmet = user(3L);
    private Comment ayseninYorumu;

    @BeforeEach
    void setUp() {
        commentService = new CommentServiceImpl(commentRepository, tweetRepository, userRepository);

        Tweet bruskunTweeti = new Tweet();
        bruskunTweeti.setId(10L);
        bruskunTweeti.setUser(brusk);

        ayseninYorumu = new Comment();
        ayseninYorumu.setId(100L);
        ayseninYorumu.setUser(ayse);
        ayseninYorumu.setTweet(bruskunTweeti);
    }

    @Test
    void delete_yorumSahibiSilebilir() {
        when(commentRepository.findById(100L)).thenReturn(Optional.of(ayseninYorumu));

        commentService.delete(ayse.getId(), 100L);

        verify(commentRepository).delete(ayseninYorumu);
    }

    @Test
    void delete_tweetSahibiDeSilebilir() {
        when(commentRepository.findById(100L)).thenReturn(Optional.of(ayseninYorumu));

        commentService.delete(brusk.getId(), 100L);

        verify(commentRepository).delete(ayseninYorumu);
    }

    @Test
    void delete_alakasizKullanici_403Firlatir() {
        when(commentRepository.findById(100L)).thenReturn(Optional.of(ayseninYorumu));

        TwitterException ex = assertThrows(TwitterException.class, () -> commentService.delete(mehmet.getId(), 100L));

        assertEquals(HttpStatus.FORBIDDEN, ex.getStatus());
        verify(commentRepository, never()).delete(any());
    }

    @Test
    void update_tweetSahibiBileBaskasininYorumunuDuzenleyemez() {
        when(commentRepository.findById(100L)).thenReturn(Optional.of(ayseninYorumu));

        TwitterException ex = assertThrows(TwitterException.class,
                () -> commentService.update(brusk.getId(), 100L, new CommentUpdateRequest("degistirdim")));

        assertEquals(HttpStatus.FORBIDDEN, ex.getStatus());
        verify(commentRepository, never()).saveAndFlush(any());
    }

    private static User user(Long id) {
        User u = new User();
        u.setId(id);
        u.setUsername("user" + id);
        return u;
    }
}
