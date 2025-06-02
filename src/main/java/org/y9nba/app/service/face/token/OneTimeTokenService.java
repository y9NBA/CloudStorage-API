package org.y9nba.app.service.face;

import org.y9nba.app.constant.OneTimeTokenType;
import org.y9nba.app.dao.entity.User;

import java.util.UUID;

public interface OneTimeTokenService {
    UUID createActivationToken(User user);
    UUID createResetPasswordToken(User user);
    UUID createRollbackPasswordToken(User user);
    UUID createRollbackEmailToken(User user, String oldEmail);
    UUID createUpdateEmailToken(User user, String newEmail);
    String findTokenById(UUID oneTimeTokenId);
    void checkOneTimeToken(String token, OneTimeTokenType type);
    void revokeOneTimeToken(String token);
    void revokeAllOneTimeTokenWithType(Long userId, OneTimeTokenType type);
    String extractEmailFromToken(String token);
    String extractHashPasswordFromToken(String token);
    Long extractUserIdFromToken(String token);
}
