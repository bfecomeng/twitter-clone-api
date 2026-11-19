package com.workintech.twitter.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

// Client'tan gelen JSON'un sekli. Entity'yi dogrudan disari acmiyoruz.
public record RegisterRequest(
        @NotBlank(message = "Kullanici adi bos olamaz")
        @Size(min = 3, max = 30, message = "Kullanici adi 3-30 karakter olmali")
        String username,

        @NotBlank(message = "Email bos olamaz")
        @Email(message = "Gecerli bir email giriniz")
        String email,

        @NotBlank(message = "Sifre bos olamaz")
        @Size(min = 6, max = 72, message = "Sifre 6-72 karakter olmali")
        String password
) {
}
