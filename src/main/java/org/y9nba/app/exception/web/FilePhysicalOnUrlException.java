package org.y9nba.app.exception.web;

import org.springframework.http.HttpStatus;

public class FilePhysicalOnUrlException extends AbstractException {

    public FilePhysicalOnUrlException(String fileUrl) {
        super("Файл с таким адресом физически существует. Адрес:" + fileUrl + "; Файлы обновлены.", HttpStatus.CONFLICT);
    }
}
