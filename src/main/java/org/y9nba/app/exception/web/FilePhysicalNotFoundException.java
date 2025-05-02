package org.y9nba.app.exception.web;

import org.springframework.http.HttpStatus;

public class FilePhysicalNotFoundException extends AbstractException {

    public FilePhysicalNotFoundException(String fileURL) {
        super("Файл удален или перемещен. Файл: " + fileURL, HttpStatus.NOT_FOUND);
    }
}
