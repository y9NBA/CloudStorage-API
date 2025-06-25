package org.y9nba.app.exception.web.provider;

import org.springframework.http.HttpStatus;
import org.y9nba.app.constant.ProviderType;
import org.y9nba.app.exception.web.AbstractException;

public class ProviderNotExistException extends AbstractException {

    public ProviderNotExistException(ProviderType type) {
        super("Аккаунт не имеет привязки к " + type.getProviderName(), HttpStatus.NOT_FOUND);
    }
}
