package com.workintech.twitter.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

// Guncellemede tweetId yok: yorum baska bir tweet'e "tasinamaz".
public record CommentUpdateRequest(
        @NotBlank(message = "Yorum bos olamaz")
        @Size(max = 280, message = "Yorum en fazla 280 karakter olabilir")
        String content
) {
}
