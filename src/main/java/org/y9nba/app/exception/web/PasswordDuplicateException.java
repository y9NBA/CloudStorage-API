package org.y9nba.app.exception;

import org.springframework.http.HttpStatus;

public class PasswordDuplicateException extends AbstractException {

    public PasswordDuplicateException() {
        super("Новый пароль должен отличаться от текущего", HttpStatus.CONFLICT);
    }
}
