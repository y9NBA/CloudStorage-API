package org.y9nba.app.exception.web.file;

import org.springframework.http.HttpStatus;
import org.y9nba.app.exception.web.AbstractException;

import java.util.Set;

public class ZipNotCreatingException extends AbstractException {

    public ZipNotCreatingException() {
        super("Произошла ошибка при создании zip-архива, пожалуйста, повторите попытку позже", HttpStatus.INTERNAL_SERVER_ERROR);
    }

    public ZipNotCreatingException(Set<String> filesURLs) {
        super(
                "Не удалось создать zip-архив, пожалуйста, обновите хранилище и повторите попытку. Ненайденные файлы: " + String.join(", ", filesURLs),
                HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
