package org.y9nba.app.exception.web.file.access;

import org.springframework.http.HttpStatus;
import org.y9nba.app.exception.web.AbstractException;

public class FileAccessAlreadyException extends AbstractException {

    public FileAccessAlreadyException(String collaboratorName) {
        super("У пользователя " + collaboratorName + " уже есть данный доступ к этому файлу", HttpStatus.BAD_REQUEST);
    }
}
