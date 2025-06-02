package org.y9nba.app.service.face.email;

public interface AccountInfoService {
    void sendAdminAccountInfo(String email, String username, String password);
    void sendUserOAuth2AccountInfo(String email, String username, String password);
}
