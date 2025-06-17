package org.y9nba.app.exception.web.file.access;

import org.springframework.http.HttpStatus;
import org.y9nba.app.exception.web.AbstractException;

public class FileAccessPublicAlreadyException extends AbstractException {

    public FileAccessPublicAlreadyException(String fileUrl) {
        super("Файл " + fileUrl + " уже публичный", HttpStatus.BAD_REQUEST);
    }
}
