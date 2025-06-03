package org.y9nba.app.exception.web.admin;

import org.springframework.http.HttpStatus;
import org.y9nba.app.exception.web.AbstractException;

public class NotFoundAdminByIdException extends AbstractException {

    public NotFoundAdminByIdException(Long adminId) {
        super("Администратор с id = " + adminId + " не найден", HttpStatus.NOT_FOUND);
    }
}
