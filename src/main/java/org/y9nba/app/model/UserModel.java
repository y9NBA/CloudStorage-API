package org.y9nba.app.model;

import jakarta.persistence.*;
import lombok.*;
import org.y9nba.app.dto.auditlog.AuditLogDto;
import org.y9nba.app.dto.file.FileDto;
import org.y9nba.app.dto.fileaccess.FileAccessDto;
import org.y9nba.app.dto.user.UserCreateDto;
import org.y9nba.app.dto.user.UserDto;
import org.y9nba.app.dto.user.UserUpdateDto;
import org.y9nba.app.dto.userrole.UserRoleDto;
import org.y9nba.app.mapper.GeneralMapper;

import java.time.LocalDateTime;
import java.util.Set;
import java.util.stream.Collectors;

@Entity
@Table(name = "\"user\"")
@Getter
@Setter
@NoArgsConstructor
public class UserModel {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String username;

    @Column(nullable = false)
    private String password;

    @Column(unique = true, nullable = false)
    private String email;

    @Column(name = "storage_limit", nullable = false)
    private Long storageLimit = 1073741824L;

    @Column(name = "used_storage", nullable = false)
    private Long usedStorage = 0L;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    @OneToMany(mappedBy = "user")
    private Set<UserRoleModel> userRoles;

    @OneToMany(mappedBy = "user")
    private Set<FileModel> files;

    @OneToMany(mappedBy = "user")
    private Set<AuditLogModel> auditLogs;

    @OneToMany(mappedBy = "user")
    private Set<FileAccessModel> fileAccesses;

    public UserModel(UserCreateDto dto) {
        this.username = dto.getUsername();
        this.password = dto.getPassword();
        this.email = dto.getEmail();
    }

    public UserModel(UserUpdateDto dto) {
        this.username = dto.getUsername();
        this.password = dto.getPassword();
        this.email = dto.getEmail();

    }
}
