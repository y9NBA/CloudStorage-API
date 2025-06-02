package org.y9nba.app.dao.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.y9nba.app.constant.OneTimeTokenType;
import org.y9nba.app.dao.entity.OneTimeToken;
import org.y9nba.app.dao.entity.User;

import java.util.Optional;
import java.util.Set;
import java.util.UUID;

@Repository
public interface OneTimeTokenRepository extends JpaRepository<OneTimeToken, UUID> {

    Optional<OneTimeToken> findByIdAndUser_IdAndType(UUID id, Long userId, OneTimeTokenType type);
    Set<OneTimeToken> findAllByUser_Id(Long userId);
    Set<OneTimeToken> findAllByUser_IdAndType(Long userId, OneTimeTokenType type);

    UUID user(User user);
}
