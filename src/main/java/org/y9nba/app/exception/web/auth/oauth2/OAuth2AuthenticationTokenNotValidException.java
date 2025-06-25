package org.y9nba.app.exception.web.auth.oauth2;

import org.springframework.http.HttpStatus;
import org.y9nba.app.exception.web.AbstractException;

public class OAuth2AuthenticationTokenNotValidException extends AbstractException {

    public OAuth2AuthenticationTokenNotValidException() {
        super("Неверный токен авторизации OAuth2, повторите авторизацию", HttpStatus.UNAUTHORIZED);
    }
}
