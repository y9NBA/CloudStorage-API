package org.y9nba.app.exception.web.file;

import org.springframework.http.HttpStatus;
import org.y9nba.app.exception.web.AbstractException;

public class FileNewUrlAlreadyException extends AbstractException {

    public FileNewUrlAlreadyException() {
        super("Невозможно переместить файл по новому адресу, файл с таким же именем и адресом уже существует", HttpStatus.CONFLICT);
    }
}
