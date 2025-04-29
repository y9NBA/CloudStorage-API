package org.y9nba.app.exception;

import org.springframework.http.HttpStatus;

public class NotFoundEntryException extends AbstractException {
    public NotFoundEntryException(String message) {
        super(message, HttpStatus.NOT_FOUND);
    }
}
