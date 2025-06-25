package org.y9nba.app.service.face.provider;

import org.y9nba.app.constant.ProviderType;
import org.y9nba.app.dao.entity.Provider;

import java.util.Set;

public interface ProviderSearchService {
    Provider getProvider(Long userId, ProviderType providerType, Long providerId);
    Provider getProvider(Long userId, ProviderType providerType);
    Set<Provider> getProvidersByUserId(Long userId);
}
