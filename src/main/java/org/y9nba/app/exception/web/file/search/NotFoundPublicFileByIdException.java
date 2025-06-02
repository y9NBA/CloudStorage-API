package org.y9nba.app.exception.web.file.search;

import org.springframework.http.HttpStatus;
import org.y9nba.app.exception.web.AbstractException;

public class NotFoundPublicFileByIdException extends AbstractException {

    public NotFoundPublicFileByIdException(Long fileId) {
        super("Не найден публичный файл с id = " + fileId, HttpStatus.NOT_FOUND);
    }
}
