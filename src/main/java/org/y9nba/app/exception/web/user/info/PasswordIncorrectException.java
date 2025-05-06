package org.y9nba.app.exception.web.user.info;

import org.springframework.http.HttpStatus;
import org.y9nba.app.exception.web.AbstractException;

public class PasswordIncorrectException extends AbstractException {

    public PasswordIncorrectException() {
        super("Пароль введен неверно", HttpStatus.BAD_REQUEST);
    }
}
