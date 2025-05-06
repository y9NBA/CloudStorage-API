package org.y9nba.app.exception.web.user.info;

import org.springframework.http.HttpStatus;
import org.y9nba.app.exception.web.AbstractException;

public class PasswordDuplicateException extends AbstractException {

    public PasswordDuplicateException() {
        super("Новый пароль должен отличаться от текущего", HttpStatus.CONFLICT);
    }
}
