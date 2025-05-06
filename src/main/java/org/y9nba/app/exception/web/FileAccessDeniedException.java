package org.y9nba.app.exception.web;

import org.springframework.http.HttpStatus;
import org.y9nba.app.constant.Access;

public class FileAccessDeniedException extends AbstractException {

    public FileAccessDeniedException() {
        super("К файлу нет доступа", HttpStatus.FORBIDDEN);
    }

    public FileAccessDeniedException(Access access) {
        super("Вы не имеете к файлу доступа " + access.name(), HttpStatus.FORBIDDEN);
    }
}
