package org.y9nba.app.exception.web.user.info.password;

import org.springframework.http.HttpStatus;
import org.y9nba.app.exception.web.AbstractException;
import org.y9nba.app.util.PasswordUtil;

public class NotValidPasswordException extends AbstractException {

    public NotValidPasswordException() {
        super(
                "Пароль должен должен состоять минимум из 8 символов, содержать минимум 1 цифру, 1 букву верхнего и нижнего регистра, а также хотя бы один из спец символов: "
                + PasswordUtil.getSpecialCharacters(),
                HttpStatus.BAD_REQUEST
        );
    }
}
