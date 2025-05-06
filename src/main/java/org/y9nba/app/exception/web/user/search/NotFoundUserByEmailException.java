package org.y9nba.app.exception.web.user.search;

import org.y9nba.app.exception.web.AbstractException;

public class NotFoundUserByEmailException extends AbstractException {

    public NotFoundUserByEmailException(String email) {
        super("Пользователь с почтой '" + email + "' не найден");
    }
}
