package org.y9nba.app.exception.web;

import org.springframework.http.HttpStatus;

public class FilePhysicalOnNewUrlException extends AbstractException {

    public FilePhysicalOnNewUrlException(String newFileURL) {
        super("Файл с таким адресом физически существует. Адрес:" + newFileURL + "; Файлы обновлены.", HttpStatus.CONFLICT);
    }
}
