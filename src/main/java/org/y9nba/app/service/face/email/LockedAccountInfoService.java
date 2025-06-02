package org.y9nba.app.service.face.email;

public interface LockedAccountInfoService {
    void sendBanAccountInfo(String email, String username);
    void sendUnbanAccountInfo(String email, String username);
}
