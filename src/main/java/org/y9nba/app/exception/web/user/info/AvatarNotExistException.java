package org.y9nba.app.exception.web.user.info;

import org.springframework.http.HttpStatus;
import org.y9nba.app.exception.web.AbstractException;

public class AvatarNotExistException extends AbstractException {

    public AvatarNotExistException() {
        super("У профиля отсутствует аватар", HttpStatus.NOT_FOUND);
    }
}
