package org.y9nba.app.service;

import org.y9nba.app.constant.OneTimeTokenType;
import org.y9nba.app.model.OneTimeTokenModel;
import org.y9nba.app.model.UserModel;

import java.util.UUID;

public interface OneTimeTokenService {
    UUID createActivationToken(UserModel userModel);
    UUID createResetPasswordToken(UserModel userModel);
    UUID createRollbackPasswordToken(UserModel userModel);
    UUID createRollbackEmailToken(UserModel userModel, String oldEmail);
    UUID createUpdateEmailToken(UserModel userModel, String newEmail);
    String findTokenById(UUID oneTimeTokenId);
    void checkOneTimeToken(String token, OneTimeTokenType type);
    void revokeOneTimeToken(String token);
    void revokeAllOneTimeTokenWithType(Long userId, OneTimeTokenType type);
    String extractEmailFromToken(String token);
    String extractHashPasswordFromToken(String token);
    Long extractUserIdFromToken(String token);
}
