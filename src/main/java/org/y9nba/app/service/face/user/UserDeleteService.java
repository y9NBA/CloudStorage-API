package org.y9nba.app.service.face.user;

public interface UserDeleteService {
    String deleteUserByEmail(String email);
    String deleteUser(Long userId, String deleteAccountToken);
}
