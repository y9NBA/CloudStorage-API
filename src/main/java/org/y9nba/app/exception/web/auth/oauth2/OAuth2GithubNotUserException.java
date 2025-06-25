package org.y9nba.app.exception.web.auth.oauth2;

import org.springframework.http.HttpStatus;
import org.y9nba.app.exception.web.AbstractException;

public class OAuth2GithubNotUserException extends AbstractException {

    public OAuth2GithubNotUserException() {
        super("Произошла какая-то ошибка при авторизации через Github", HttpStatus.UNAUTHORIZED);
    }
}
