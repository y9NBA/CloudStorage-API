package org.y9nba.app.mapper;

import org.y9nba.app.dao.entity.*;
import org.y9nba.app.dto.auditlog.AuditLogDto;
import org.y9nba.app.dto.file.FileDto;
import org.y9nba.app.dto.file.FilePresentDto;
import org.y9nba.app.dto.fileaccess.FileAccessDto;
import org.y9nba.app.dto.search.AdminInfoDto;
import org.y9nba.app.dto.search.UserInfoDto;
import org.y9nba.app.dto.search.UserSearchDto;
import org.y9nba.app.dto.session.SessionDto;
import org.y9nba.app.dto.warning.WarningDto;

import java.util.Set;
import java.util.stream.Collectors;

public class GeneralMapper {
    public static Set<WarningDto> toWarningDto(Set<Warning> models) {
        if (models == null) {
            return Set.of();
        }

        return models.stream().map(WarningDto::new).collect(Collectors.toSet());
    }

    public static Set<FileDto> toFileDto(Set<File> models) {
        if (models == null) {
            return Set.of();
        }

        return models.stream().map(FileDto::new).collect(Collectors.toSet());
    }

    public static Set<FilePresentDto> toFilePresentDto(Set<File> models) {
        if (models == null) {
            return Set.of();
        }

        return models.stream().map(FilePresentDto::new).collect(Collectors.toSet());
    }

    public static Set<FileAccessDto> toFileAccessDto(Set<FileAccess> models) {
        if (models == null) {
            return Set.of();
        }

        return models.stream().map(FileAccessDto::new).collect(Collectors.toSet());
    }

    public static Set<AdminInfoDto> toAdminInfoDto(Set<User> models) {
        if (models == null) {
            return Set.of();
        }

        return models.stream().map(AdminInfoDto::new).collect(Collectors.toSet());
    }

    public static Set<UserInfoDto> toUserInfoDto(Set<User> models) {
        if (models == null) {
            return Set.of();
        }

        return models.stream().map(UserInfoDto::new).collect(Collectors.toSet());
    }

    public static Set<UserSearchDto> toUserSearchDto(Set<User> models) {
        if (models == null) {
            return Set.of();
        }

        return models.stream().map(UserSearchDto::new).collect(Collectors.toSet());
    }

    public static Set<AuditLogDto> toAuditLogDto(Set<AuditLog> models) {
        if (models == null) {
            return Set.of();
        }

        return models.stream().map(AuditLogDto::new).collect(Collectors.toSet());
    }

    public static Set<SessionDto> toSessionDto(Set<Session> models) {
        if (models == null) {
            return Set.of();
        }

        return models.stream().map(SessionDto::new).collect(Collectors.toSet());
    }
}
