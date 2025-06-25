package org.y9nba.app.exception.web.auth.oauth2;

import org.springframework.http.HttpStatus;
import org.y9nba.app.exception.web.AbstractException;

public class OAuth2Exception extends AbstractException {

    public OAuth2Exception() {
        super("Произошла какая-то ошибка при авторизации через сторонний сервис", HttpStatus.BAD_REQUEST);
    }
}
