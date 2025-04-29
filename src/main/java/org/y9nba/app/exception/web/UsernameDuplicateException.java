package org.y9nba.app.exception.web;

import org.springframework.http.HttpStatus;

public class UsernameDuplicateException extends AbstractException {

    public UsernameDuplicateException() {
        super("Новый логин должен отличаться от текущего", HttpStatus.CONFLICT);
    }
}
