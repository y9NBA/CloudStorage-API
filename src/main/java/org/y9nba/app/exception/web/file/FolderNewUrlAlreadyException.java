package org.y9nba.app.exception.web.file;

import org.springframework.http.HttpStatus;
import org.y9nba.app.exception.web.AbstractException;

public class FolderNewUrlAlreadyException extends AbstractException {

    public FolderNewUrlAlreadyException() {
        super("Невозможно переместить папку по новому адресу или переименовать её, папка с таким же именем или адресом уже существует", HttpStatus.CONFLICT);
    }
}
