package org.y9nba.app.dto.user;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.y9nba.app.dto.auditlog.AuditLogDto;
import org.y9nba.app.dto.file.FileDto;
import org.y9nba.app.dto.fileaccess.FileAccessDto;
import org.y9nba.app.dto.userrole.UserRoleDto;
import org.y9nba.app.model.UserModel;

import java.time.LocalDateTime;
import java.util.Set;
import java.util.stream.Collectors;

@Data
@NoArgsConstructor
public class UserDto {
    private Long id;
    private String userName;
    private String password;
    private String email;
    private LocalDateTime createdAt;
    private Set<UserRoleDto> userRoles;
    private Set<FileDto> files;
    private Set<AuditLogDto> auditLogs;
    private Set<FileAccessDto> fileAccesses;

    public UserDto(UserModel model) {
        this.id = model.getId();
        this.userName = model.getUsername();
        this.password = model.getPassword();
        this.email = model.getEmail();
        this.createdAt = model.getCreatedAt();
        this.userRoles = model.getUserRoles().stream().map(UserRoleDto::new).collect(Collectors.toSet());
        this.files = model.getFiles().stream().map(FileDto::new).collect(Collectors.toSet());
        this.auditLogs = model.getAuditLogs().stream().map(AuditLogDto::new).collect(Collectors.toSet());
        this.fileAccesses = model.getFileAccesses().stream().map(FileAccessDto::new).collect(Collectors.toSet());
    }
}
