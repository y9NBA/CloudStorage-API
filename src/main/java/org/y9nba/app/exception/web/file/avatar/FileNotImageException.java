package org.y9nba.app.exception.web.file.avatar;

import org.springframework.http.HttpStatus;
import org.y9nba.app.exception.web.AbstractException;

public class FileNotImageException extends AbstractException {

    public FileNotImageException() {
        super("Загружаемый файл не является изображением", HttpStatus.BAD_REQUEST);
    }
}
