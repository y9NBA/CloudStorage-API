package org.y9nba.app.dto.userrole;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.y9nba.app.constant.Role;
import org.y9nba.app.model.UserModel;

@Getter
@AllArgsConstructor
public class UserRoleCreateDto {
    private UserModel user;
    private Role role;
}
