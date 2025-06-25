package org.y9nba.app.service.impl.provider;

import org.springframework.stereotype.Service;
import org.y9nba.app.constant.ProviderType;
import org.y9nba.app.dao.entity.Provider;
import org.y9nba.app.dao.repository.ProviderRepository;
import org.y9nba.app.exception.web.provider.ProviderNotExistException;
import org.y9nba.app.service.face.provider.ProviderSearchService;

import java.util.Set;

@Service
public class ProviderSearchServiceImpl implements ProviderSearchService {

    private final ProviderRepository providerRepository;

    public ProviderSearchServiceImpl(ProviderRepository providerRepository) {
        this.providerRepository = providerRepository;
    }

    @Override
    public Provider getProvider(Long userId, ProviderType providerType, Long providerId) {
        return providerRepository
                .findByUser_IdAndProviderTypeAndProviderId(
                        userId,
                        providerType,
                        providerId
                ).orElse(null);
    }

    @Override
    public Provider getProvider(Long userId, ProviderType providerType) {
        return providerRepository
                .findByUser_IdAndProviderType(
                        userId,
                        providerType
                ).orElseThrow(
                        () -> new ProviderNotExistException(providerType)
                );
    }

    @Override
    public Set<Provider> getProvidersByUserId(Long userId) {
        return providerRepository
                .findByUser_Id(userId);
    }
}
