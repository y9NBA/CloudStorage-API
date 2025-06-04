package org.y9nba.app.exception.web.user;

import org.springframework.http.HttpStatus;
import org.y9nba.app.exception.web.AbstractException;

public class OneTimeTokenNotValidException extends AbstractException {

    public OneTimeTokenNotValidException() {
        super("Данная ссылка устарела или уже была использована", HttpStatus.GONE);
    }
}
