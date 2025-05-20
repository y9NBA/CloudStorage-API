package org.y9nba.app.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.y9nba.app.constant.OneTimeTokenType;
import org.y9nba.app.model.OneTimeTokenModel;

import java.util.Optional;
import java.util.Set;
import java.util.UUID;

@Repository
public interface OneTimeTokenRepository extends JpaRepository<OneTimeTokenModel, UUID> {

    Optional<OneTimeTokenModel> findByToken(String token);
    Optional<OneTimeTokenModel> findByUser_IdAndTypeAndToken(Long userId, OneTimeTokenType type, String token);
    Set<OneTimeTokenModel> findAllByUser_Id(Long userId);
    Set<OneTimeTokenModel> findAllByUser_IdAndType(Long userId, OneTimeTokenType type);
}
