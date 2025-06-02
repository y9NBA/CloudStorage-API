package org.y9nba.app.service.face.admin;

import org.y9nba.app.constant.Reason;
import org.y9nba.app.dao.entity.Warning;

import java.util.Set;

public interface WarningService {
    Warning createNewWarning(Long userId, Long adminId, Reason reason);
    void revokeWarning(Long userId);
    void revokeAllWarnings(Long userId);
    Set<Warning> getAllWarningsByUserId(Long userId);
    Set<Warning> getAllActiveWarningsByUserId(Long userId);
}
