package org.y9nba.app.dto.auditlog;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.y9nba.app.constant.Action;
import org.y9nba.app.dao.entity.AuditLog;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
public class AuditLogDto {
    private Long id;
    private Long userId;
    private Action action;
    private Long fileId;
    private LocalDateTime createdAt;

    public AuditLogDto(AuditLog model) {
        this.id = model.getId();
        this.userId = model.getUser().getId();
        this.action = model.getAction();
        this.fileId = model.getFile().getId();
        this.createdAt = model.getCreatedAt();
    }
}
