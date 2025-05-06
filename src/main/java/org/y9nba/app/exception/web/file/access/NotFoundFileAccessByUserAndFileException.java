package org.y9nba.app.exception.web.file.access;

import org.springframework.http.HttpStatus;
import org.y9nba.app.exception.web.AbstractException;

public class NotFoundFileAccessByUserAndFileException extends AbstractException {

    public NotFoundFileAccessByUserAndFileException(Long fileId, Long userId) {
        super("Не найден доступ к файлу. FileId: " + fileId + "; UserId: " + userId, HttpStatus.NOT_FOUND);
    }
}
