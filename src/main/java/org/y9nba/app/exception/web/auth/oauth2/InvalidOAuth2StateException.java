package org.y9nba.app.exception.web.auth.oauth2;

import org.springframework.http.HttpStatus;
import org.y9nba.app.exception.web.AbstractException;

public class InvalidOAuth2StateException extends AbstractException {

    public InvalidOAuth2StateException() {
        super("Запрос не прошёл проверку состояния OAuth2, пожалуйста, повторите попытку", HttpStatus.BAD_REQUEST);
    }
}
