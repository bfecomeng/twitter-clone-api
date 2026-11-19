package com.workintech.twitter.dto;

import com.workintech.twitter.entity.Comment;

import java.time.LocalDateTime;

public record CommentResponse(
        Long id,
        String content,
        Long tweetId,
        Long userId,
        String username,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
    public static CommentResponse from(Comment c) {
        return new CommentResponse(c.getId(), c.getContent(), c.getTweet().getId(),
                c.getUser().getId(), c.getUser().getUsername(), c.getCreatedAt(), c.getUpdatedAt());
    }
}
