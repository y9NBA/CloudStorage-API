package org.y9nba.app.exception.web.file.search;

import org.springframework.http.HttpStatus;
import org.y9nba.app.exception.web.AbstractException;

public class NotFoundFileByURLException extends AbstractException {

    public NotFoundFileByURLException(String url) {
        super("Файл по адресу [" + url + "] не найден", HttpStatus.NOT_FOUND);
    }
}
