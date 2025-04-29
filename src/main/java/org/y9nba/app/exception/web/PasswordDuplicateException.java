package org.y9nba.app.exception.web;

import org.springframework.http.HttpStatus;

public class PasswordDuplicateException extends AbstractException {

    public PasswordDuplicateException() {
        super("Новый пароль должен отличаться от текущего", HttpStatus.CONFLICT);
    }
}
