package org.y9nba.app.exception.web;

import org.springframework.http.HttpStatus;

public class NotFoundEntryException extends AbstractException {
    public NotFoundEntryException(String message) {
        super(message, HttpStatus.NOT_FOUND);
    }
}
