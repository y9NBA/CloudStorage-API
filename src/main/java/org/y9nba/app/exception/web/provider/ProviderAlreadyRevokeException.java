package org.y9nba.app.exception.web.provider;

import org.springframework.http.HttpStatus;
import org.y9nba.app.constant.ProviderType;
import org.y9nba.app.exception.web.AbstractException;

public class ProviderAlreadyRevokeException extends AbstractException {

    public ProviderAlreadyRevokeException(ProviderType type) {
        super("Провайдер " + type.getProviderName() + " уже был отвязан или не был привязан", HttpStatus.BAD_REQUEST);
    }
}
