package org.y9nba.app.exception.web.file.avatar;

import org.springframework.http.HttpStatus;
import org.y9nba.app.exception.web.AbstractException;

public class AvatarNotUploadException extends AbstractException {

    public AvatarNotUploadException() {
        super("Ошибка при загрузке аватара", HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
