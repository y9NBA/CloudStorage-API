package org.y9nba.app.exception.web.user.search;

import org.springframework.http.HttpStatus;
import org.y9nba.app.exception.web.AbstractException;

public class NotFoundUserByIdException extends AbstractException {

    public NotFoundUserByIdException(Long userId) {
        super("Пользователь с id = " + userId + " не найден", HttpStatus.NOT_FOUND);
    }
}
