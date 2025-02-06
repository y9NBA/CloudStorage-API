package org.y9nba.app.repository;

import org.springframework.stereotype.Repository;
import org.y9nba.app.base.repository.BaseRepository;
import org.y9nba.app.model.AuditLogModel;

import java.util.Set;

@Repository
public interface AuditLogRepository extends BaseRepository<AuditLogModel, Long> {

    Set<AuditLogModel> getAuditLogModelsByUserId(Long userId);
}
