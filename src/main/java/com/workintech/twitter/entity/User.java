package com.workintech.twitter.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
// "user" PostgreSQL'de ayrilmis (reserved) bir kelime; tablo adini "users" yapiyoruz.
@Table(name = "users")
@Getter
@Setter
@NoArgsConstructor
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Kullanici adi bos olamaz")
    @Size(min = 3, max = 30, message = "Kullanici adi 3-30 karakter olmali")
    @Column(nullable = false, unique = true, length = 30)
    private String username;

    @NotBlank(message = "Email bos olamaz")
    @Email(message = "Gecerli bir email giriniz")
    @Column(nullable = false, unique = true)
    private String email;

    // Burada her zaman HASH'lenmis sifre duracak, asla duz metin degil.
    @NotBlank
    @Column(nullable = false)
    private String password;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    // Kayit veritabanina ilk kez yazilmadan hemen once JPA bu metodu cagirir.
    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
    }
}
