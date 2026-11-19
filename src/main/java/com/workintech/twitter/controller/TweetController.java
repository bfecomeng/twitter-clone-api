package com.workintech.twitter.controller;

import com.workintech.twitter.dto.TweetRequest;
import com.workintech.twitter.dto.TweetResponse;
import com.workintech.twitter.service.TweetService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/tweet")
public class TweetController {

    private final TweetService tweetService;

    public TweetController(TweetService tweetService) {
        this.tweetService = tweetService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public TweetResponse create(@AuthenticationPrincipal Jwt jwt, @Valid @RequestBody TweetRequest request) {
        return tweetService.create(currentUserId(jwt), request);
    }

    // GET /tweet/findByUserId?userId=1
    @GetMapping("/findByUserId")
    public List<TweetResponse> findByUserId(@RequestParam Long userId) {
        return tweetService.findByUserId(userId);
    }

    // GET /tweet/findById?id=5
    @GetMapping("/findById")
    public TweetResponse findById(@RequestParam Long id) {
        return tweetService.findById(id);
    }

    @PutMapping("/{id}")
    public TweetResponse update(@AuthenticationPrincipal Jwt jwt, @PathVariable Long id,
                                @Valid @RequestBody TweetRequest request) {
        return tweetService.update(currentUserId(jwt), id, request);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@AuthenticationPrincipal Jwt jwt, @PathVariable Long id) {
        tweetService.delete(currentUserId(jwt), id);
    }

    // Kullanici kimligi: imzali token'in "sub" alani.
    private Long currentUserId(Jwt jwt) {
        return Long.valueOf(jwt.getSubject());
    }
}
