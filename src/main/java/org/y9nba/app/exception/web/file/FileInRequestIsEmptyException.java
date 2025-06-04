package org.y9nba.app.exception.web.file;

import org.springframework.http.HttpStatus;
import org.y9nba.app.exception.web.AbstractException;

public class FileInRequestIsEmptyException extends AbstractException {

    public FileInRequestIsEmptyException() {
        super("Файл не может быть пустым", HttpStatus.BAD_REQUEST);
    }
}
