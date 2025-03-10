package org.y9nba.app.exception;

import org.springframework.http.HttpStatus;

public class UsernameAlreadyException extends AbstractException {
    public UsernameAlreadyException() {
        super("Имя пользователя уже занято", HttpStatus.CONFLICT);
    }
}
