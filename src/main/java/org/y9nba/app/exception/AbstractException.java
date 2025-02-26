package org.y9nba.app.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;

@Getter
public class AbstractException extends RuntimeException {
    private final String message;
    private final HttpStatusCode statusCode;

    public AbstractException(String message) {
        this(message, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    public AbstractException(String message, HttpStatusCode statusCode) {
        this.message = message;
        this.statusCode = statusCode;
    }
}
