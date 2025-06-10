package org.y9nba.app.exception.web.file;

import lombok.Getter;
import org.springframework.http.HttpStatus;
import org.y9nba.app.exception.web.AbstractException;

@Getter
public class FilePhysicalNotFoundException extends AbstractException {

    private final String fileURL;

    public FilePhysicalNotFoundException(String fileURL) {
        super("Файл удален или перемещен. Файл: " + fileURL, HttpStatus.NOT_FOUND);
        this.fileURL = fileURL;
    }
}
