package org.y9nba.app.constant;

import lombok.Getter;

@Getter
public enum OneTimeTokenType {
    ACTIVATION(getDefaultExpireTime()),
    UPDATE_EMAIL(getDefaultExpireTime()),
    ROLLBACK_EMAIL(getRollbackExpireTime()),
    DELETE_ACCOUNT(getDefaultExpireTime()),
    RESET_PASSWORD(getDefaultExpireTime()),
    ROLLBACK_PASSWORD(getRollbackExpireTime());

    private final Long expireTime;

    OneTimeTokenType(Long expireTime) {
        this.expireTime = expireTime;
    }

    private static Long getDefaultExpireTime() {
        return 3600000L;    // 1 час
    }

    private static Long getRollbackExpireTime() {
        return 86400000L;    // 1 день (24 часа)
    }
}