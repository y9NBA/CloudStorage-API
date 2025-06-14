package org.y9nba.app.exception.web.user.info.email;

import org.springframework.http.HttpStatus;
import org.y9nba.app.exception.web.AbstractException;

public class NotValidEmailException extends AbstractException {

    public NotValidEmailException() {
        super("Некорректный адрес электронной почты", HttpStatus.BAD_REQUEST);
    }
}
