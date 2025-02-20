package org.y9nba.app.mapper;

import org.y9nba.app.dto.auditlog.AuditLogDto;
import org.y9nba.app.dto.file.FileDto;
import org.y9nba.app.dto.fileaccess.FileAccessDto;
import org.y9nba.app.dto.user.UserDto;
import org.y9nba.app.dto.userrole.UserRoleDto;
import org.y9nba.app.model.*;

import java.util.Set;
import java.util.stream.Collectors;

public class GeneralMapper {
    public static Set<UserRoleDto> toUserRoleDto(Set<UserRoleModel> models) {
        if (models == null) {
            return Set.of();
        }

        return models.stream().map(UserRoleDto::new).collect(Collectors.toSet());
    }

    public static Set<FileDto> toFileDto(Set<FileModel> models) {
        if (models == null) {
            return Set.of();
        }

        return models.stream().map(FileDto::new).collect(Collectors.toSet());
    }

    public static Set<FileAccessDto> toFileAccessDto(Set<FileAccessModel> models) {
        if (models == null) {
            return Set.of();
        }

        return models.stream().map(FileAccessDto::new).collect(Collectors.toSet());
    }

    public static Set<UserDto> toUserDto(Set<UserModel> models) {
        if (models == null) {
            return Set.of();
        }

        return models.stream().map(UserDto::new).collect(Collectors.toSet());
    }

    public static Set<AuditLogDto> toAuditLogDto(Set<AuditLogModel> models) {
        if (models == null) {
            return Set.of();
        }

        return models.stream().map(AuditLogDto::new).collect(Collectors.toSet());
    }
}
