package org.y9nba.app.dao.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.y9nba.app.constant.ProviderType;
import org.y9nba.app.dao.entity.Provider;
import org.y9nba.app.dao.entity.embedded.ProviderPK;

import java.util.Optional;
import java.util.Set;

@Repository
public interface ProviderRepository extends JpaRepository<Provider, ProviderPK> {
    Optional<Provider> findByUser_IdAndProviderType(Long userId, ProviderType providerType);
    Optional<Provider> findByUser_IdAndProviderTypeAndProviderId(Long userId, ProviderType providerType, Long providerId);
    Set<Provider> findByUser_Id(Long userId);
}
