package org.y9nba.app.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;

public class UnAuthorizedException extends AbstractException {
    public UnAuthorizedException() {
        super("UNAUTHORIZED", HttpStatus.UNAUTHORIZED);
    }
}
