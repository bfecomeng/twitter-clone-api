package com.workintech.twitter.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record CommentCreateRequest(
        @NotNull(message = "tweetId zorunlu") Long tweetId,
        @NotBlank(message = "Yorum bos olamaz")
        @Size(max = 280, message = "Yorum en fazla 280 karakter olabilir")
        String content
) {
}
