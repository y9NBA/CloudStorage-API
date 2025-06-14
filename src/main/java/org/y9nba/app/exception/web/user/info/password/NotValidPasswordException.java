package org.y9nba.app.exception.web.user.info.password;

import org.springframework.http.HttpStatus;
import org.y9nba.app.exception.web.AbstractException;

public class NotValidPasswordException extends AbstractException {

    public NotValidPasswordException() {
        super(
                "Пароль должен должен состоять минимум из 8 символов, содержать минимум 1 цифру, 1 букву верхнего и нижнего регистра, а также хотя бы один из спец символов: -_/@$!%*?&",
                HttpStatus.BAD_REQUEST
        );
    }
}
