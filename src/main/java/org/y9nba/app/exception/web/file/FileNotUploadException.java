package org.y9nba.app.exception.web.file;

import org.springframework.http.HttpStatus;
import org.y9nba.app.exception.web.AbstractException;

public class FileNotUploadException extends AbstractException {

    public FileNotUploadException() {
        super("Не удалось загрузить файл", HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
