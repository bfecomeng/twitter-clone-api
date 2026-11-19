package com.workintech.twitter.controller;

import com.workintech.twitter.dto.CommentCreateRequest;
import com.workintech.twitter.dto.CommentResponse;
import com.workintech.twitter.dto.CommentUpdateRequest;
import com.workintech.twitter.service.CommentService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/comment")
public class CommentController {

    private final CommentService commentService;

    public CommentController(CommentService commentService) {
        this.commentService = commentService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public CommentResponse create(@AuthenticationPrincipal Jwt jwt, @Valid @RequestBody CommentCreateRequest request) {
        return commentService.create(currentUserId(jwt), request);
    }

    @PutMapping("/{id}")
    public CommentResponse update(@AuthenticationPrincipal Jwt jwt, @PathVariable Long id,
                                  @Valid @RequestBody CommentUpdateRequest request) {
        return commentService.update(currentUserId(jwt), id, request);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@AuthenticationPrincipal Jwt jwt, @PathVariable Long id) {
        commentService.delete(currentUserId(jwt), id);
    }

    private Long currentUserId(Jwt jwt) {
        return Long.valueOf(jwt.getSubject());
    }
}
