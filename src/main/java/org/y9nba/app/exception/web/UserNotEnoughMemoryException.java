package org.y9nba.app.exception.web;

import org.springframework.http.HttpStatus;

public class UserNotEnoughMemoryException extends AbstractException {

    public UserNotEnoughMemoryException() {
        super("Недостаточно памяти для загрузки файла", HttpStatus.CONFLICT);
    }
}
