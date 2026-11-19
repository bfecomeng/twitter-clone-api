package com.workintech.twitter.service;

import com.workintech.twitter.dto.LoginRequest;
import com.workintech.twitter.dto.LoginResponse;
import com.workintech.twitter.dto.RegisterRequest;
import com.workintech.twitter.dto.UserResponse;
import com.workintech.twitter.entity.User;
import com.workintech.twitter.exception.TwitterException;
import com.workintech.twitter.repository.UserRepository;
import com.workintech.twitter.security.TokenService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthServiceImplTest {

    @Mock
    private UserRepository userRepository;
    @Mock
    private PasswordEncoder passwordEncoder;
    @Mock
    private TokenService tokenService;

    private AuthServiceImpl authService;

    @BeforeEach
    void setUp() {
        // Constructor injection sayesinde Spring'e ihtiyac duymadan, mock'larla olusturabiliyoruz.
        authService = new AuthServiceImpl(userRepository, passwordEncoder, tokenService);
    }

    @Test
    void register_basarili_sifreyiHashleyipKaydeder() {
        // Arrange (hazirla)
        RegisterRequest request = new RegisterRequest("brusk", "brusk@test.com", "123456");
        when(userRepository.existsByUsername("brusk")).thenReturn(false);
        when(userRepository.existsByEmail("brusk@test.com")).thenReturn(false);
        when(passwordEncoder.encode("123456")).thenReturn("HASHLENMIS");
        when(userRepository.save(any(User.class))).thenAnswer(inv -> {
            User u = inv.getArgument(0);
            u.setId(1L);
            return u;
        });

        // Act (calistir)
        UserResponse response = authService.register(request);

        // Assert (dogrula)
        assertEquals(1L, response.id());
        assertEquals("brusk", response.username());

        // Veritabanina giden User nesnesini yakalayip sifresine bakiyoruz.
        ArgumentCaptor<User> captor = ArgumentCaptor.forClass(User.class);
        verify(userRepository).save(captor.capture());
        assertEquals("HASHLENMIS", captor.getValue().getPassword());
        assertNotEquals("123456", captor.getValue().getPassword());
    }

    @Test
    void register_kullaniciAdiAlinmis_409FirlatirVeKaydetmez() {
        RegisterRequest request = new RegisterRequest("brusk", "yeni@test.com", "123456");
        when(userRepository.existsByUsername("brusk")).thenReturn(true);

        TwitterException ex = assertThrows(TwitterException.class, () -> authService.register(request));

        assertEquals(HttpStatus.CONFLICT, ex.getStatus());
        verify(userRepository, never()).save(any());
    }

    @Test
    void login_dogruSifre_tokenDoner() {
        User user = new User();
        user.setId(1L);
        user.setUsername("brusk");
        user.setPassword("HASH");
        when(userRepository.findByUsername("brusk")).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("123456", "HASH")).thenReturn(true);
        when(tokenService.generateToken(user)).thenReturn("jwt-token");
        when(tokenService.getExpirationSeconds()).thenReturn(3600L);

        LoginResponse response = authService.login(new LoginRequest("brusk", "123456"));

        assertEquals("jwt-token", response.token());
        assertEquals("Bearer", response.tokenType());
        assertEquals(3600L, response.expiresIn());
    }

    @Test
    void login_yanlisSifre_401FirlatirVeTokenUretmez() {
        User user = new User();
        user.setUsername("brusk");
        user.setPassword("HASH");
        when(userRepository.findByUsername("brusk")).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("yanlis", "HASH")).thenReturn(false);

        TwitterException ex = assertThrows(TwitterException.class,
                () -> authService.login(new LoginRequest("brusk", "yanlis")));

        assertEquals(HttpStatus.UNAUTHORIZED, ex.getStatus());
        verify(tokenService, never()).generateToken(any());
    }

    @Test
    void login_olmayanKullanici_yanlisSifreyleAyniMesajiVerir() {
        when(userRepository.findByUsername("yok")).thenReturn(Optional.empty());

        TwitterException ex = assertThrows(TwitterException.class,
                () -> authService.login(new LoginRequest("yok", "123456")));

        assertEquals(HttpStatus.UNAUTHORIZED, ex.getStatus());
        assertEquals("Kullanici adi veya sifre hatali", ex.getMessage());
    }
}
