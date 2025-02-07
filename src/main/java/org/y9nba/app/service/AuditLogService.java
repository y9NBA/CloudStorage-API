package org.y9nba.app.service;


import org.y9nba.app.dto.auditlog.AuditLogCreateDto;
import org.y9nba.app.dto.auditlog.AuditLogDto;
import org.y9nba.app.dto.user.UserDto;

import java.util.Set;

public interface AuditLogService {
    AuditLogDto save(AuditLogCreateDto entity);
    void delete(AuditLogDto entity);
    void deleteById(Long id);
    AuditLogDto findById(Long id);
    boolean existsById(Long id);
    Set<AuditLogDto> findByUser(Long userId);
}