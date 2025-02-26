package org.y9nba.app.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.y9nba.app.model.TokenModel;
import org.y9nba.app.model.UserModel;

import java.util.Optional;
import java.util.Set;

@Repository
public interface TokenRepository extends JpaRepository<TokenModel, Long> {

    Optional<TokenModel> findByAccessToken(String accessToken);
    Optional<TokenModel> findByRefreshToken(String refreshToken);
    Set<TokenModel> findAllByUser(UserModel user);
}
