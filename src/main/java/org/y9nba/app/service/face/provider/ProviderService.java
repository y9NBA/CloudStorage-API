package org.y9nba.app.service.face.provider;

import org.y9nba.app.constant.ProviderType;
import org.y9nba.app.dao.entity.Provider;
import org.y9nba.app.dao.entity.User;

public interface ProviderService {
    Provider linkProviderAtUser(User user, ProviderType providerType, Long providerId);
    void checkProviderLinked(Long userId, ProviderType providerType, Long providerId);
    void revokeProviderByUserId(Long userId, ProviderType providerType, Long providerId);
}
