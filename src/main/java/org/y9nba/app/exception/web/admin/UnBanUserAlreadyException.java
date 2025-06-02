package org.y9nba.app.exception.web.admin;

import org.springframework.http.HttpStatus;
import org.y9nba.app.exception.web.AbstractException;

public class UnBanUserAlreadyException extends AbstractException {

    public UnBanUserAlreadyException() {
        super("Пользователь уже разбанен", HttpStatus.BAD_REQUEST);
    }
}
