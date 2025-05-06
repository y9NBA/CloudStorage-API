package org.y9nba.app.exception.web.user.search;

import org.springframework.http.HttpStatus;
import org.y9nba.app.exception.web.AbstractException;

public class NotFoundUserByUsernameException extends AbstractException {

    public NotFoundUserByUsernameException(String username) {
        super("Пользователь c именем '" + username + "' не найден", HttpStatus.NOT_FOUND);
    }
}
