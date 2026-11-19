package com.workintech.twitter.controller;

import com.workintech.twitter.dto.LoginRequest;
import com.workintech.twitter.dto.LoginResponse;
import com.workintech.twitter.dto.RegisterRequest;
import com.workintech.twitter.dto.UserResponse;
import com.workintech.twitter.service.AuthService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

@RestController
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    // @Valid: RegisterRequest'teki @NotBlank, @Email vb. kurallari calistirir.
    @PostMapping("/register")
    @ResponseStatus(HttpStatus.CREATED)
    public UserResponse register(@Valid @RequestBody RegisterRequest request) {
        return authService.register(request);
    }

    @PostMapping("/login")
    public LoginResponse login(@Valid @RequestBody LoginRequest request) {
        return authService.login(request);
    }

    // Korumali endpoint. @AuthenticationPrincipal: filtrenin dogruladigi token'i verir.
    // Kullanici kimligini ISTEK GOVDESINDEN degil, IMZALI TOKEN'dan aliyoruz.
    @GetMapping("/me")
    public UserResponse me(@AuthenticationPrincipal Jwt jwt) {
        Long userId = Long.valueOf(jwt.getSubject());
        return authService.getUser(userId);
    }
}
