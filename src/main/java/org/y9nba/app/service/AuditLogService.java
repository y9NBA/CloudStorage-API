package org.y9nba.app.service;


import org.y9nba.app.dto.auditlog.AuditLogCreateDto;
import org.y9nba.app.dto.auditlog.AuditLogDto;
import org.y9nba.app.model.AuditLogModel;
import org.y9nba.app.model.FileModel;
import org.y9nba.app.model.UserModel;

import java.util.Set;

public interface AuditLogService {
    AuditLogModel save(AuditLogCreateDto entity);
    void logDownload(UserModel user, FileModel file);
    void logCreate(UserModel user, FileModel file);
    void logUpdate(UserModel user, FileModel file);
    void logRename(UserModel user, FileModel file);
    void logMove(UserModel user, FileModel file);
    void logAddAccess(UserModel user, FileModel file);
    void logRemoveAccess(UserModel user, FileModel file);
    void delete(AuditLogModel entity);
    void deleteById(Long id);
    AuditLogModel findById(Long id);
    boolean existsById(Long id);
    Set<AuditLogDto> findByUser(Long userId);
}