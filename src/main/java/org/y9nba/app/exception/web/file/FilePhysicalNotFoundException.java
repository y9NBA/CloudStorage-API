package org.y9nba.app.exception.web.file;

import org.springframework.http.HttpStatus;
import org.y9nba.app.exception.web.AbstractException;

public class FilePhysicalNotFoundException extends AbstractException {

    public FilePhysicalNotFoundException(String fileURL) {
        super("Файл удален или перемещен. Файл: " + fileURL, HttpStatus.NOT_FOUND);
    }
}
