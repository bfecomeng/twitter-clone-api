package com.workintech.twitter.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

// Dikkat: userId alani YOK. Tweet'in sahibi her zaman token'dan belirlenir.
public record TweetRequest(
        @NotBlank(message = "Tweet bos olamaz")
        @Size(max = 280, message = "Tweet en fazla 280 karakter olabilir")
        String content
) {
}
