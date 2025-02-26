package org.y9nba.app.exception;

import org.springframework.http.HttpStatus;

public class EmailAlreadyException extends AbstractException {
    public EmailAlreadyException() {
        super("Такой email уже занят", HttpStatus.BAD_REQUEST);
    }
}
