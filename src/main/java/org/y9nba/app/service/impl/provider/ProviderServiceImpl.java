package org.y9nba.app.service.impl.provider;

import org.springframework.stereotype.Service;
import org.y9nba.app.constant.ProviderType;
import org.y9nba.app.dao.entity.Provider;
import org.y9nba.app.dao.entity.User;
import org.y9nba.app.dao.entity.embedded.ProviderPK;
import org.y9nba.app.dao.repository.ProviderRepository;
import org.y9nba.app.exception.web.provider.ProviderAlreadyRevokeException;
import org.y9nba.app.exception.web.provider.ProviderNotExistException;
import org.y9nba.app.service.face.provider.ProviderSearchService;
import org.y9nba.app.service.face.provider.ProviderService;

@Service
public class ProviderServiceImpl implements ProviderService {

    private final ProviderRepository providerRepository;
    private final ProviderSearchService providerSearchService;

    public ProviderServiceImpl(ProviderRepository providerRepository, ProviderSearchService providerSearchService) {
        this.providerRepository = providerRepository;
        this.providerSearchService = providerSearchService;
    }

    @Override
    public Provider linkProviderAtUser(User user, ProviderType providerType, Long providerId) {
        Provider provider = providerSearchService.getProvider(user.getId(), providerType, providerId);

        if (provider == null) {
            provider = new Provider();
            ProviderPK pk = new ProviderPK(
                    user.getId(),
                    providerType
            );

            provider.setId(pk);
            provider.setUser(user);
        }

        provider.setProviderId(providerId);

        return providerRepository.save(provider);
    }

    @Override
    public void checkProviderLinked(Long userId, ProviderType providerType, Long providerId) {
        Provider provider = providerSearchService.getProvider(userId, providerType, providerId);

        if (provider == null) {
            throw new ProviderNotExistException(providerType);
        }
    }

    @Override
    public void revokeProviderByUserId(Long userId, ProviderType providerType, Long providerId) {
        Provider provider = providerSearchService.getProvider(userId, providerType, providerId);

        if (provider == null) {
            throw new ProviderAlreadyRevokeException(providerType);
        }

        providerRepository.delete(provider);
    }
}
