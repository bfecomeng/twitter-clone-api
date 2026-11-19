package com.workintech.twitter.exception;

import org.springframework.http.HttpStatus;

// Uygulamamizin tum "beklenen" hatalari bu tipte firlatilir;
// hangi HTTP status ile donecegini de yaninda tasir.
public class TwitterException extends RuntimeException {

    private final HttpStatus status;

    public TwitterException(String message, HttpStatus status) {
        super(message);
        this.status = status;
    }

    public HttpStatus getStatus() {
        return status;
    }
}
