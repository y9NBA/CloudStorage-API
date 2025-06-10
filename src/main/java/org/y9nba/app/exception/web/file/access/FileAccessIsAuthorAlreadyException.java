package org.y9nba.app.exception.web.file.access;

import org.springframework.http.HttpStatus;
import org.y9nba.app.exception.web.AbstractException;

public class FileAccessIsAuthorAlreadyException extends AbstractException {

    public FileAccessIsAuthorAlreadyException() {
        super("Вы автор данного файла, нельзя поменять свой доступ к нему", HttpStatus.BAD_REQUEST);
    }
}
