package com.workintech.twitter.service;

import com.workintech.twitter.dto.CommentCreateRequest;
import com.workintech.twitter.dto.CommentResponse;
import com.workintech.twitter.dto.CommentUpdateRequest;
import com.workintech.twitter.entity.Comment;
import com.workintech.twitter.entity.Tweet;
import com.workintech.twitter.entity.User;
import com.workintech.twitter.exception.TwitterException;
import com.workintech.twitter.repository.CommentRepository;
import com.workintech.twitter.repository.TweetRepository;
import com.workintech.twitter.repository.UserRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class CommentServiceImpl implements CommentService {

    private final CommentRepository commentRepository;
    private final TweetRepository tweetRepository;
    private final UserRepository userRepository;

    public CommentServiceImpl(CommentRepository commentRepository, TweetRepository tweetRepository,
                              UserRepository userRepository) {
        this.commentRepository = commentRepository;
        this.tweetRepository = tweetRepository;
        this.userRepository = userRepository;
    }

    @Override
    @Transactional
    public CommentResponse create(Long currentUserId, CommentCreateRequest request) {
        User user = userRepository.findById(currentUserId)
                .orElseThrow(() -> new TwitterException("Kullanici bulunamadi", HttpStatus.NOT_FOUND));
        Tweet tweet = tweetRepository.findById(request.tweetId())
                .orElseThrow(() -> new TwitterException("Tweet bulunamadi: " + request.tweetId(), HttpStatus.NOT_FOUND));

        Comment comment = new Comment();
        comment.setContent(request.content());
        comment.setUser(user);
        comment.setTweet(tweet);
        return CommentResponse.from(commentRepository.save(comment));
    }

    @Override
    @Transactional
    public CommentResponse update(Long currentUserId, Long commentId, CommentUpdateRequest request) {
        Comment comment = findCommentOrThrow(commentId);
        // Yorumu SADECE yazan duzenleyebilir (tweet sahibi bile baskasinin agzina soz koyamaz).
        if (!comment.getUser().getId().equals(currentUserId)) {
            throw new TwitterException("Sadece kendi yorumunuzu duzenleyebilirsiniz", HttpStatus.FORBIDDEN);
        }
        comment.setContent(request.content());
        return CommentResponse.from(commentRepository.saveAndFlush(comment));
    }

    @Override
    @Transactional
    public void delete(Long currentUserId, Long commentId) {
        Comment comment = findCommentOrThrow(commentId);
        boolean isCommentOwner = comment.getUser().getId().equals(currentUserId);
        boolean isTweetOwner = comment.getTweet().getUser().getId().equals(currentUserId);
        // README: yorumu, yorumun sahibi VEYA tweet'in sahibi silebilir.
        if (!isCommentOwner && !isTweetOwner) {
            throw new TwitterException("Bu yorumu silme yetkiniz yok", HttpStatus.FORBIDDEN);
        }
        commentRepository.delete(comment);
    }

    private Comment findCommentOrThrow(Long commentId) {
        return commentRepository.findById(commentId)
                .orElseThrow(() -> new TwitterException("Yorum bulunamadi: " + commentId, HttpStatus.NOT_FOUND));
    }
}
