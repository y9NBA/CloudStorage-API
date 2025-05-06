package org.y9nba.app.exception.web.user;

import org.springframework.http.HttpStatus;
import org.y9nba.app.exception.web.AbstractException;

public class UserNotEnoughMemoryException extends AbstractException {

    public UserNotEnoughMemoryException() {
        super("Недостаточно памяти для загрузки файла", HttpStatus.CONFLICT);
    }
}
