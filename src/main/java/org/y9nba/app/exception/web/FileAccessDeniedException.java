package org.y9nba.app.exception.web;

import org.springframework.http.HttpStatus;

public class FileAccessDeniedException extends AbstractException {

    public FileAccessDeniedException() {
        super("К файлу нет доступа", HttpStatus.FORBIDDEN);
    }
}
