package org.y9nba.app.dto.auditlog;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.y9nba.app.constant.Action;
import org.y9nba.app.dao.entity.File;
import org.y9nba.app.dao.entity.User;

@AllArgsConstructor
@Getter
public class AuditLogCreateDto {
    private User user;
    private File file;
    private Action action;
}
