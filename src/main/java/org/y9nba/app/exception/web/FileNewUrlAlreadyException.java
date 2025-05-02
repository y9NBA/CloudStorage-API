package org.y9nba.app.exception.web;

import org.springframework.http.HttpStatus;

public class FileNewUrlAlreadyException extends AbstractException {

    public FileNewUrlAlreadyException() {
        super("Невозможно переместить файл по новому адресу. Файл с таким же именем и адресом уже существует.", HttpStatus.CONFLICT);
    }
}
