package org.y9nba.app.exception.web.admin;

import org.springframework.http.HttpStatus;
import org.y9nba.app.exception.web.AbstractException;

public class UserNotHaveActiveWarningsException extends AbstractException {

    public UserNotHaveActiveWarningsException(Long userId) {
        super("Пользователь с id " + userId + " не имеет активных предупреждений", HttpStatus.CONFLICT);
    }
}
