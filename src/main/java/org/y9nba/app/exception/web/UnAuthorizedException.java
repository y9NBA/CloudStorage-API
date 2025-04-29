package org.y9nba.app.exception.web;

import org.springframework.http.HttpStatus;

public class UnAuthorizedException extends AbstractException {
    public UnAuthorizedException() {
        super("UNAUTHORIZED", HttpStatus.UNAUTHORIZED);
    }
}
