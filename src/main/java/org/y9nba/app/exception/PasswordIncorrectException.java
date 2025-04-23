package org.y9nba.app.exception;

import org.springframework.http.HttpStatus;

public class PasswordIncorrectException extends AbstractException {

    public PasswordIncorrectException() {
        super("Пароль введен неверно", HttpStatus.BAD_REQUEST);
    }
}
