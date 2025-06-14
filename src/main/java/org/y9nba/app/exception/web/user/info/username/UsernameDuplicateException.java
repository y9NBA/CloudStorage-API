package org.y9nba.app.exception.web.user.info.username;

import org.springframework.http.HttpStatus;
import org.y9nba.app.exception.web.AbstractException;

public class UsernameDuplicateException extends AbstractException {

    public UsernameDuplicateException() {
        super("Новый логин должен отличаться от текущего", HttpStatus.CONFLICT);
    }
}
