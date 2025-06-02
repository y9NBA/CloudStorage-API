package org.y9nba.app.exception.web.admin;

import org.springframework.http.HttpStatus;
import org.y9nba.app.exception.web.AbstractException;

public class UserIsAdminException extends AbstractException {

    public UserIsAdminException() {
        super("Данный пользователь является администратором", HttpStatus.BAD_REQUEST);
    }
}
