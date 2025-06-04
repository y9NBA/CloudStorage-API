package org.y9nba.app.exception.web.file;

import org.springframework.http.HttpStatus;
import org.y9nba.app.exception.web.AbstractException;

public class FilePhysicalOnUrlException extends AbstractException {

    public FilePhysicalOnUrlException(String fileUrl) {
        super("Файл с таким адресом физически существует. Адрес:" + fileUrl + "; Файлы обновлены", HttpStatus.CONFLICT);
    }
}
