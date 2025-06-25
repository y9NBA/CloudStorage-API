package org.y9nba.app.exception.web.auth;

import org.springframework.http.HttpStatus;
import org.y9nba.app.exception.web.AbstractException;

public class OAuth2GoogleNotUserException extends AbstractException {

    public OAuth2GoogleNotUserException() {
        super("Произошла какая-то ошибка при авторизации через Google", HttpStatus.UNAUTHORIZED);
    }
}
