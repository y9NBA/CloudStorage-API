package org.y9nba.app.service.face.admin;

public interface BanService {
    void banUser(Long userId);
    void unbanUser(Long userId);
}
