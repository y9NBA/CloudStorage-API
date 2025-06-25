package org.y9nba.app.dto.provider;

import lombok.Getter;
import org.y9nba.app.constant.ProviderType;
import org.y9nba.app.dao.entity.Provider;

import java.time.LocalDateTime;

@Getter
public class ProviderDto {
    private final Long userId;
    private final ProviderType providerType;
    private final Long providerId;
    private final LocalDateTime createdAt;

    public ProviderDto(Provider provider) {
        this.userId = provider.getId().getUserId();
        this.providerType = provider.getProviderType();
        this.providerId = provider.getProviderId();
        this.createdAt = provider.getCreatedAt();
    }
}
