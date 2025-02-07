package org.y9nba.app.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.y9nba.app.model.AuditLogModel;

import java.util.Set;

@Repository
public interface AuditLogRepository extends JpaRepository<AuditLogModel, Long> {

    Set<AuditLogModel> getAuditLogModelsByUserId(Long userId);
}
