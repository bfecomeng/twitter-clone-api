package com.workintech.twitter.service;

import com.workintech.twitter.dto.LoginRequest;
import com.workintech.twitter.dto.LoginResponse;
import com.workintech.twitter.dto.RegisterRequest;
import com.workintech.twitter.dto.UserResponse;
import com.workintech.twitter.entity.User;
import com.workintech.twitter.exception.TwitterException;
import com.workintech.twitter.repository.UserRepository;
import com.workintech.twitter.security.TokenService;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final TokenService tokenService;

    // Constructor injection: bagimliliklar final, nesne her zaman tam kurulu,
    // testte de new AuthServiceImpl(mockRepo, mockEncoder, mockToken) ile kolayca olusturulur.
    public AuthServiceImpl(UserRepository userRepository, PasswordEncoder passwordEncoder, TokenService tokenService) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.tokenService = tokenService;
    }

    @Override
    @Transactional
    public UserResponse register(RegisterRequest request) {
        if (userRepository.existsByUsername(request.username())) {
            throw new TwitterException("Bu kullanici adi zaten alinmis", HttpStatus.CONFLICT);
        }
        if (userRepository.existsByEmail(request.email())) {
            throw new TwitterException("Bu email zaten kayitli", HttpStatus.CONFLICT);
        }

        User user = new User();
        user.setUsername(request.username());
        user.setEmail(request.email());
        // Duz sifre asla veritabanina gitmez.
        user.setPassword(passwordEncoder.encode(request.password()));

        User saved = userRepository.save(user);
        return UserResponse.from(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public LoginResponse login(LoginRequest request) {
        // Kullanici yoksa da sifre yanlissa da AYNI mesaj: saldirgan hangi
        // kullanici adlarinin var oldugunu deneme-yanilmayla ogrenemesin.
        User user = userRepository.findByUsername(request.username())
                .orElseThrow(() -> new TwitterException("Kullanici adi veya sifre hatali", HttpStatus.UNAUTHORIZED));

        // matches: girilen duz sifreyi ayni salt ile hash'leyip DB'deki hash ile karsilastirir.
        if (!passwordEncoder.matches(request.password(), user.getPassword())) {
            throw new TwitterException("Kullanici adi veya sifre hatali", HttpStatus.UNAUTHORIZED);
        }

        String token = tokenService.generateToken(user);
        return new LoginResponse(token, "Bearer", tokenService.getExpirationSeconds(), UserResponse.from(user));
    }

    @Override
    @Transactional(readOnly = true)
    public UserResponse getUser(Long userId) {
        return userRepository.findById(userId)
                .map(UserResponse::from)
                .orElseThrow(() -> new TwitterException("Kullanici bulunamadi", HttpStatus.NOT_FOUND));
    }
}
