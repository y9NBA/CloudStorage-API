package org.y9nba.app.exception.web.session;

import org.springframework.http.HttpStatus;
import org.y9nba.app.exception.web.AbstractException;

public class NotUseRevokeSessionOnCurrentSession extends AbstractException {

    public NotUseRevokeSessionOnCurrentSession() {
        super("Вы не можете завершить текущую сессию, для завершения текущей сессии просто выйдите из аккаунта", HttpStatus.BAD_REQUEST);
    }
}
