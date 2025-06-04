package org.y9nba.app.exception.web.auth;

import org.springframework.http.HttpStatus;
import org.y9nba.app.exception.web.AbstractException;

public class AccountLockedException extends AbstractException {

    public AccountLockedException() {
        super("Учетная запись пользователя заблокирована", HttpStatus.FORBIDDEN);
    }
}
