package org.y9nba.app.service.face.token;

import org.y9nba.app.constant.OneTimeTokenType;
import org.y9nba.app.dao.entity.User;

public interface OneTimeTokenService {
    String createActivationToken(User user);
    String createResetPasswordToken(User user);
    String createRollbackPasswordToken(User user);
    String createRollbackEmailToken(User user, String oldEmail);
    String createUpdateEmailToken(User user, String newEmail);
    String createDeleteAccountToken(User user);
    void checkOneTimeToken(String token, OneTimeTokenType type);
    void revokeOneTimeToken(String token);
    void revokeAllOneTimeTokenWithType(Long userId, OneTimeTokenType type);
    String extractEmailFromToken(String token);
    String extractHashPasswordFromToken(String token);
    Long getUserIdByOneTimeToken(String token);
}
