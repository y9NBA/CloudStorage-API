package org.y9nba.app.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.Set;

@Entity
@Table(name = "user")
@Data
@NoArgsConstructor
public class UserModel {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false, length = 255)
    private String username;

    @Column(nullable = false)
    private String password;

    @Column(unique = true, nullable = false, length = 255)
    private String email;

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
}
