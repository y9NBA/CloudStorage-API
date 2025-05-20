package org.y9nba.app.exception.web.user.info;

import org.springframework.http.HttpStatus;
import org.y9nba.app.exception.web.AbstractException;

public class ActiveAlreadyException extends AbstractException {

    public ActiveAlreadyException() {
        super("Учетная запись уже активирована", HttpStatus.BAD_REQUEST);
    }
}
