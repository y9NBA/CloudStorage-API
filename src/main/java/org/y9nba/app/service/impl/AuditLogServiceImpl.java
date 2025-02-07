package org.y9nba.app.service.impl;

import org.springframework.stereotype.Service;
import org.y9nba.app.dto.auditlog.AuditLogCreateDto;
import org.y9nba.app.dto.auditlog.AuditLogDto;
import org.y9nba.app.dto.user.UserDto;
import org.y9nba.app.repository.AuditLogRepository;
import org.y9nba.app.service.AuditLogService;

import java.util.Set;

@Service
public class AuditLogServiceImpl implements AuditLogService {

    private final AuditLogRepository repository;

    public AuditLogServiceImpl(AuditLogRepository repository) {
        this.repository = repository;
    }

    @Override
    public AuditLogDto save(AuditLogCreateDto entity) {
        return null;
    }

    @Override
    public void delete(AuditLogDto entity) {

    }

    @Override
    public void deleteById(Long id) {

    }

    @Override
    public AuditLogDto findById(Long id) {
        return null;
    }

    @Override
    public boolean existsById(Long id) {
        return false;
    }

    @Override
    public Set<AuditLogDto> findByUser(Long userId) {
        return null;
    }
}
