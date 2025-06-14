package org.y9nba.app.exception.web.user.info.username;

import org.springframework.http.HttpStatus;
import org.y9nba.app.exception.web.AbstractException;

public class NotValidUsernameException extends AbstractException {

    public NotValidUsernameException() {
        super("Логин должен иметь длину от 3 до 30 символов, а также может содержать цифры и символы: _.%-", HttpStatus.BAD_REQUEST);
    }
}
