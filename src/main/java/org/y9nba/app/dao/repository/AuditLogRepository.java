package org.y9nba.app.dao.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.y9nba.app.dao.entity.AuditLog;

import java.util.Set;

@Repository
public interface AuditLogRepository extends JpaRepository<AuditLog, Long> {

    Set<AuditLog> getAuditLogsByUserId(Long userId);
}
