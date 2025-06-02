package org.y9nba.app.service.face.admin;

public interface ModerationService {
    void revokePublicFileWithWarning(Long fileId, Long adminId);
    void revokePublicFileWithNotification(Long fileId);
}
