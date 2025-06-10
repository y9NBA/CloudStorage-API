package org.y9nba.app.exception.web.file;

import org.springframework.http.HttpStatus;
import org.y9nba.app.exception.web.AbstractException;

public class FolderNotExistException extends AbstractException {

    public FolderNotExistException(String folderURL) {
        super("Папки не существует по адресу: " + folderURL, HttpStatus.NOT_FOUND);
    }
}
