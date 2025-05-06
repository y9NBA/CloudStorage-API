package org.y9nba.app.exception.web.file.access;

import org.springframework.http.HttpStatus;
import org.y9nba.app.constant.Access;
import org.y9nba.app.exception.web.AbstractException;

public class FileAccessDeniedException extends AbstractException {

    public FileAccessDeniedException() {
        super("К файлу нет доступа", HttpStatus.FORBIDDEN);
    }

    public FileAccessDeniedException(Access access) {
        super("Вы не имеете к файлу доступа " + access.name(), HttpStatus.FORBIDDEN);
    }
}
