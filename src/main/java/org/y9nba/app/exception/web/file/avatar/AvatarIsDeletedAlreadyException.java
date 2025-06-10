package org.y9nba.app.exception.web.file.avatar;

import org.springframework.http.HttpStatus;
import org.y9nba.app.exception.web.AbstractException;

public class AvatarIsDeletedAlreadyException extends AbstractException {

    public AvatarIsDeletedAlreadyException() {
        super("Аватар уже удален", HttpStatus.BAD_REQUEST);
    }
}
