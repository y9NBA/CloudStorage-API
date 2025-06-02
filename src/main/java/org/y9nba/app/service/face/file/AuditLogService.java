package org.y9nba.app.service.face;


import org.y9nba.app.dto.auditlog.AuditLogCreateDto;
import org.y9nba.app.dto.auditlog.AuditLogDto;
import org.y9nba.app.dao.entity.AuditLog;
import org.y9nba.app.dao.entity.File;
import org.y9nba.app.dao.entity.User;

import java.util.Set;

public interface AuditLogService {
    AuditLog save(AuditLogCreateDto entity);
    void logDownload(User user, File file);
    void logCreate(User user, File file);
    void logUpdate(User user, File file);
    void logRename(User user, File file);
    void logMove(User user, File file);
    void logCopy(User user, File file);
    void logAddAccess(User user, File file);
    void logRemoveAccess(User user, File file);
    void logMakePublic(User user, File file);
    void logMakePrivate(User user, File file);
    void delete(AuditLog entity);
    void deleteById(Long id);
    AuditLog findById(Long id);
    boolean existsById(Long id);
    Set<AuditLogDto> findByUser(Long userId);
}