package org.y9nba.app.exception.web.token;

import org.springframework.http.HttpStatus;
import org.y9nba.app.exception.web.AbstractException;

public class TokenNotValidException extends AbstractException {

    public TokenNotValidException() {
        super("Невалидный токен", HttpStatus.BAD_REQUEST);
    }

}
