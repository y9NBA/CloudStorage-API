package org.y9nba.app.dto.user;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.y9nba.app.constant.Role;

@Getter
@AllArgsConstructor
public class UserRoleDto {
    private final Role role;
}
