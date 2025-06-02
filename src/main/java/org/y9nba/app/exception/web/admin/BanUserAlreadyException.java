package org.y9nba.app.exception.web.admin;

import org.springframework.http.HttpStatus;
import org.y9nba.app.exception.web.AbstractException;

public class BanUserAlreadyException extends AbstractException {

    public BanUserAlreadyException() {
        super("Пользователь уже забанен", HttpStatus.BAD_REQUEST);
    }
}
