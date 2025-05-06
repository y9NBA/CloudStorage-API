package org.y9nba.app.exception.web.auth;

import org.springframework.http.HttpStatus;
import org.y9nba.app.exception.web.AbstractException;

public class UnAuthorizedException extends AbstractException {
    public UnAuthorizedException() {
        super("UNAUTHORIZED", HttpStatus.UNAUTHORIZED);
    }
}
