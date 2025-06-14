package org.y9nba.app.exception.web.user.info.email;

import org.springframework.http.HttpStatus;
import org.y9nba.app.exception.web.AbstractException;

public class EmailAlreadyException extends AbstractException {
    public EmailAlreadyException() {
        super("Такой email уже занят", HttpStatus.CONFLICT);
    }
}
