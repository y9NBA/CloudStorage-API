package org.y9nba.app.exception.web.user.info.email;

import org.springframework.http.HttpStatus;
import org.y9nba.app.exception.web.AbstractException;

public class EmailDuplicateException extends AbstractException {

    public EmailDuplicateException() {
        super("Новый email должен отличаться от текущего", HttpStatus.CONFLICT);
    }
}
