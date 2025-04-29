package org.y9nba.app.exception.web;

import org.springframework.http.HttpStatus;

public class FileNotUploadException extends AbstractException {

    public FileNotUploadException() {
        super("Не удалось загрузить файл", HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
