package org.y9nba.app.exception.web.auth;

import org.springframework.http.HttpStatus;
import org.y9nba.app.exception.web.AbstractException;

public class NotValidEmailException extends AbstractException {

    public NotValidEmailException() {
        super("Не очень похоже на корректный email", HttpStatus.BAD_REQUEST);
    }
}
