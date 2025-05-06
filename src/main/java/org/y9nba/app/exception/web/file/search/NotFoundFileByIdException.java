package org.y9nba.app.exception.web.file.search;

import org.springframework.http.HttpStatus;
import org.y9nba.app.exception.web.AbstractException;

public class NotFoundFileByIdException extends AbstractException {

    public NotFoundFileByIdException(Long fileId) {
        super("Не найден файл с id = " + fileId, HttpStatus.NOT_FOUND);
    }
}
