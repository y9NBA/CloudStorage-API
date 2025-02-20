package org.y9nba.app.dto.user;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.y9nba.app.dto.auditlog.AuditLogDto;
import org.y9nba.app.dto.file.FileDto;
import org.y9nba.app.dto.fileaccess.FileAccessDto;
import org.y9nba.app.dto.userrole.UserRoleDto;
import org.y9nba.app.model.UserModel;

import java.time.LocalDateTime;
import java.util.Set;
import java.util.stream.Collectors;

@Setter
@Getter
@NoArgsConstructor
public class UserUpdateDto {
    private String username;
    private String password;
    private String email;

    public UserUpdateDto(UserModel model) {
        this.username = model.getUsername();
        this.password = model.getPassword();
        this.email = model.getEmail();
    }
}
