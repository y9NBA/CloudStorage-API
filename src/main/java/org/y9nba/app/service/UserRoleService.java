package org.y9nba.app.service;

import org.y9nba.app.base.service.BaseService;
import org.y9nba.app.constant.Role;
import org.y9nba.app.model.UserModel;
import org.y9nba.app.model.UserRoleModel;

public interface UserRoleService extends BaseService<UserRoleModel, UserRoleModel.UserRoleId> {
    UserRoleModel save(UserModel user, Role role);
}
