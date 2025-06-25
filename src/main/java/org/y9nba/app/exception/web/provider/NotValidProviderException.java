package org.y9nba.app.exception.web.provider;

import org.springframework.http.HttpStatus;
import org.y9nba.app.constant.ProviderType;
import org.y9nba.app.exception.web.AbstractException;

import java.util.Arrays;

public class NotValidProviderException extends AbstractException {

    public NotValidProviderException(String invalidProvider) {
        super(
                "Некорректный провайдер: " + invalidProvider + ". Доступные провайдеры: " + Arrays.toString(ProviderType.values()),
                HttpStatus.BAD_REQUEST
        );
    }
}
