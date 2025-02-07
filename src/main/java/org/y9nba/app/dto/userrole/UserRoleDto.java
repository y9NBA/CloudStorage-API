package org.y9nba.app.dto.userrole;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.y9nba.app.constant.Role;
import org.y9nba.app.model.UserRoleModel;

@Data
@NoArgsConstructor
public class UserRoleDto {
    private Long userId;
    private Role role;

    public UserRoleDto(UserRoleModel model) {
        this.userId = model.getId().getUserId();
        this.role = model.getId().getRole();
    }
}
