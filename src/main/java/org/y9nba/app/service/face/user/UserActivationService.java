package org.y9nba.app.service.face.user;

public interface UserActivationService {
    String resendActivationByEmail(String email);
    String activateUser(Long userId, String activateToken);
}
