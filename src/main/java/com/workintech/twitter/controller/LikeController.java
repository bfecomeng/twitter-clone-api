package com.workintech.twitter.controller;

import com.workintech.twitter.dto.LikeResponse;
import com.workintech.twitter.dto.TweetIdRequest;
import com.workintech.twitter.service.LikeService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

// README'deki adresler: POST /like ve POST /dislike
@RestController
public class LikeController {

    private final LikeService likeService;

    public LikeController(LikeService likeService) {
        this.likeService = likeService;
    }

    @PostMapping("/like")
    @ResponseStatus(HttpStatus.CREATED)
    public LikeResponse like(@AuthenticationPrincipal Jwt jwt, @Valid @RequestBody TweetIdRequest request) {
        return likeService.like(currentUserId(jwt), request.tweetId());
    }

    @PostMapping("/dislike")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void dislike(@AuthenticationPrincipal Jwt jwt, @Valid @RequestBody TweetIdRequest request) {
        likeService.dislike(currentUserId(jwt), request.tweetId());
    }

    private Long currentUserId(Jwt jwt) {
        return Long.valueOf(jwt.getSubject());
    }
}
