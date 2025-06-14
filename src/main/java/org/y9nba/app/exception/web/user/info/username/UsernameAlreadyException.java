package org.y9nba.app.exception.web.user.info.username;

import org.springframework.http.HttpStatus;
import org.y9nba.app.exception.web.AbstractException;

public class UsernameAlreadyException extends AbstractException {
    public UsernameAlreadyException() {
        super("Имя пользователя уже занято", HttpStatus.CONFLICT);
    }
}
