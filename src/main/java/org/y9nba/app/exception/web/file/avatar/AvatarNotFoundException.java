package org.y9nba.app.exception.web.file.avatar;

import org.springframework.http.HttpStatus;
import org.y9nba.app.exception.web.AbstractException;

public class AvatarNotFoundException extends AbstractException {

    public AvatarNotFoundException(String avatarName) {
        super("Аватар с таким именем не найден. Аватар: " + avatarName, HttpStatus.NOT_FOUND);
    }
}
