package org.y9nba.app.service.impl;

import org.springframework.stereotype.Service;
import org.y9nba.app.base.service.BaseServiceImpl;
import org.y9nba.app.model.AuditLogModel;
import org.y9nba.app.repository.AuditLogRepository;
import org.y9nba.app.service.AuditLogService;

import java.util.Set;

@Service
public class AuditLogServiceImpl extends BaseServiceImpl<AuditLogRepository, AuditLogModel, Long> implements AuditLogService {

    public AuditLogServiceImpl(AuditLogRepository repository) {
        super(repository);
    }

    @Override
    public Set<AuditLogModel> findByUser(Long userId) {
        return repository.getAuditLogModelsByUserId(userId);
    }
}
