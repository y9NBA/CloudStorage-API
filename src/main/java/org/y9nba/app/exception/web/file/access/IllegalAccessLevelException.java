package org.y9nba.app.exception.web.file.access;

import org.springframework.http.HttpStatus;
import org.y9nba.app.exception.web.AbstractException;

public class IllegalAccessLevelException extends AbstractException {

    public IllegalAccessLevelException() {
        super("Неправильно указан уровень доступа (0 - только чтение, 1 - чтение и запись)", HttpStatus.BAD_REQUEST);
    }
}
