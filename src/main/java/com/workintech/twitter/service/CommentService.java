package com.workintech.twitter.service;

import com.workintech.twitter.dto.CommentCreateRequest;
import com.workintech.twitter.dto.CommentResponse;
import com.workintech.twitter.dto.CommentUpdateRequest;

public interface CommentService {

    CommentResponse create(Long currentUserId, CommentCreateRequest request);

    CommentResponse update(Long currentUserId, Long commentId, CommentUpdateRequest request);

    void delete(Long currentUserId, Long commentId);
}
