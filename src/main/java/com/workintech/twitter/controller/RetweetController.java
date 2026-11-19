package com.workintech.twitter.controller;

import com.workintech.twitter.dto.RetweetResponse;
import com.workintech.twitter.dto.TweetIdRequest;
import com.workintech.twitter.service.RetweetService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/retweet")
public class RetweetController {

    private final RetweetService retweetService;

    public RetweetController(RetweetService retweetService) {
        this.retweetService = retweetService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public RetweetResponse retweet(@AuthenticationPrincipal Jwt jwt, @Valid @RequestBody TweetIdRequest request) {
        return retweetService.retweet(currentUserId(jwt), request.tweetId());
    }

    // {id} = retweet'in kendi id'si (orijinal tweet'in degil)
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@AuthenticationPrincipal Jwt jwt, @PathVariable Long id) {
        retweetService.delete(currentUserId(jwt), id);
    }

    private Long currentUserId(Jwt jwt) {
        return Long.valueOf(jwt.getSubject());
    }
}
