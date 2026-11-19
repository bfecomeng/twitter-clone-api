package com.workintech.twitter.service;

import com.workintech.twitter.dto.LoginRequest;
import com.workintech.twitter.dto.LoginResponse;
import com.workintech.twitter.dto.RegisterRequest;
import com.workintech.twitter.dto.UserResponse;

// Controller bu arayuzu bilir, implementasyonu bilmez -> test ederken mock'lamak kolay.
public interface AuthService {

    UserResponse register(RegisterRequest request);

    LoginResponse login(LoginRequest request);

    UserResponse getUser(Long userId);
}
