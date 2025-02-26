package org.y9nba.app.model;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.y9nba.app.dto.auth.RegistrationRequestDto;
import org.y9nba.app.dto.user.UserCreateDto;
import org.y9nba.app.dto.user.UserUpdateDto;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.Set;
import java.util.stream.Collectors;

@Entity
@Table(name = "\"user\"")
@Getter
@Setter
@NoArgsConstructor
public class UserModel implements UserDetails {
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
    private Long storageLimit = 1073741824L;    // 1gb in byte

    @Column(name = "used_storage", nullable = false)
    private Long usedStorage = 0L;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    @OneToMany(mappedBy = "user", fetch = FetchType.EAGER)
    private Set<UserRoleModel> userRoles;

    @OneToMany(mappedBy = "user", fetch = FetchType.EAGER)
    private Set<FileModel> files;

    @OneToMany(mappedBy = "user", fetch = FetchType.EAGER)
    private Set<AuditLogModel> auditLogs;

    @OneToMany(mappedBy = "user", fetch = FetchType.EAGER)
    private Set<FileAccessModel> fileAccesses;

    @OneToMany(mappedBy = "user", fetch = FetchType.EAGER)
    private Set<TokenModel> tokens;

    public UserModel(UserUpdateDto dto) {
        this.username = dto.getUsername();
        this.password = dto.getPassword();
        this.email = dto.getEmail();

    }

    public UserModel(UserCreateDto dto) {
        this.username = dto.getUsername();
        this.password = dto.getHashPassword();
        this.email = dto.getEmail();
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return userRoles
                .stream()
                .map(UserRoleModel::getId)
                .map(UserRoleModel.UserRoleId::getRole)
                .map(Enum::name)
                .map(SimpleGrantedAuthority::new)
                .collect(Collectors.toList());
    }

    @Override
    public boolean isAccountNonExpired() {
        return UserDetails.super.isAccountNonExpired();
    }

    @Override
    public boolean isAccountNonLocked() {
        return UserDetails.super.isAccountNonLocked();
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return UserDetails.super.isCredentialsNonExpired();
    }

    @Override
    public boolean isEnabled() {
        return UserDetails.super.isEnabled();
    }
}
