package org.y9nba.app.exception.web.file;

import org.springframework.http.HttpStatus;
import org.y9nba.app.exception.web.AbstractException;

public class FileStorageException extends AbstractException {

    public FileStorageException() {
        super("Файловое хранилище сейчас недоступно, пожалуйста, повторите попытку позже.", HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
