package org.y9nba.app.exception;

import org.springframework.http.HttpStatus;

public class EmailDuplicateException extends AbstractException {

    public EmailDuplicateException() {
        super("Новый email должен отличаться от текущего", HttpStatus.CONFLICT);
    }
}
