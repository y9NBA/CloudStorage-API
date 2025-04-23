package org.y9nba.app.dto.auditlog;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.y9nba.app.constant.Action;
import org.y9nba.app.model.FileModel;
import org.y9nba.app.model.UserModel;

@AllArgsConstructor
@Getter
public class AuditLogCreateDto {
    private UserModel user;
    private FileModel file;
    private Action action;
}
