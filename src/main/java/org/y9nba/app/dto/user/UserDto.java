package org.y9nba.app.dto.user;

import lombok.Getter;
import lombok.NoArgsConstructor;
import org.y9nba.app.constant.Role;
import org.y9nba.app.dto.auditlog.AuditLogDto;
import org.y9nba.app.dto.file.FileDto;
import org.y9nba.app.dto.fileaccess.FileAccessDto;
import org.y9nba.app.mapper.GeneralMapper;
import org.y9nba.app.dao.entity.User;

import java.time.LocalDateTime;
import java.util.Set;
import java.util.UUID;

@Getter
@NoArgsConstructor
public class UserDto {
    private Long id;
    private String username;
    private String email;
    private UUID bucketName;
    private Long storageLimit;
    private Long usedStorage;
    private LocalDateTime createdAt;
    private Role role;
    private Set<FileDto> files;
    private Set<AuditLogDto> auditLogs;
    private Set<FileAccessDto> fileAccesses;

    public UserDto(User model) {
        this.id = model.getId();
        this.username = model.getUsername();
        this.email = model.getEmail();
        this.bucketName = UUID.fromString(model.getBucketName());
        this.storageLimit = model.getStorageLimit();
        this.usedStorage = model.getUsedStorage();
        this.createdAt = model.getCreatedAt();
        this.role = model.getRole();
        this.files = GeneralMapper.toFileDto(model.getFiles());
        this.auditLogs = GeneralMapper.toAuditLogDto(model.getAuditLogs());
        this.fileAccesses = GeneralMapper.toFileAccessDto(model.getFileAccesses());
    }
}
