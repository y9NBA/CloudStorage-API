package org.y9nba.app.exception.web;

import org.springframework.http.HttpStatus;

public class UsernameAlreadyException extends AbstractException {
    public UsernameAlreadyException() {
        super("Имя пользователя уже занято", HttpStatus.CONFLICT);
    }
}
