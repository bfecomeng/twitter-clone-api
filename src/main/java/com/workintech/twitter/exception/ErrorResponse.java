package com.workintech.twitter.exception;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.time.LocalDateTime;
import java.util.Map;

// Tum hatalar client'a AYNI sekilde doner; frontend tek bir format bilir.
// fieldErrors sadece validation hatalarinda dolu; null ise JSON'a hic yazilmaz.
@JsonInclude(JsonInclude.Include.NON_NULL)
public record ErrorResponse(
        int status,
        String message,
        String path,
        LocalDateTime timestamp,
        Map<String, String> fieldErrors
) {
    public ErrorResponse(int status, String message, String path) {
        this(status, message, path, LocalDateTime.now(), null);
    }
}
