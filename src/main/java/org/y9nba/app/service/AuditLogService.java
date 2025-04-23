package org.y9nba.app.service;


import org.y9nba.app.dto.auditlog.AuditLogCreateDto;
import org.y9nba.app.dto.auditlog.AuditLogDto;
import org.y9nba.app.dto.user.UserDto;
import org.y9nba.app.model.AuditLogModel;

import java.util.Set;

public interface AuditLogService {
    AuditLogModel save(AuditLogCreateDto entity);
    void delete(AuditLogModel entity);
    void deleteById(Long id);
    AuditLogModel findById(Long id);
    boolean existsById(Long id);
    Set<AuditLogDto> findByUser(Long userId);
}