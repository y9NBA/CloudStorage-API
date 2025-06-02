package org.y9nba.app.service.face.email;

public interface PublicFileInfoService {
    void sendWarningInfo(String email, String fileURL);
    void sendNotificationInfo(String email, String fileURL);
}
