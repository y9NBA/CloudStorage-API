package org.y9nba.app.exception.web.file.access;

import org.springframework.http.HttpStatus;
import org.y9nba.app.exception.web.AbstractException;

public class FileAccessPrivateAlreadyException extends AbstractException {

    public FileAccessPrivateAlreadyException(String fileUrl) {
        super("Файл " + fileUrl + " уже приватный", HttpStatus.BAD_REQUEST);
    }
}
