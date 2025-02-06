package org.y9nba.app.service.impl;

import org.springframework.stereotype.Service;
import org.y9nba.app.base.service.BaseServiceImpl;
import org.y9nba.app.constant.Role;
import org.y9nba.app.model.UserModel;
import org.y9nba.app.model.UserRoleModel;
import org.y9nba.app.repository.UserRoleRepository;
import org.y9nba.app.service.UserRoleService;

import java.util.Set;

@Service
public class UserRoleServiceImpl extends BaseServiceImpl<UserRoleRepository, UserRoleModel, UserRoleModel.UserRoleId> implements UserRoleService {

    public UserRoleServiceImpl(UserRoleRepository repository) {
        super(repository);
    }

    @Override
    public UserRoleModel save(UserModel user, Role role) {
        UserRoleModel userRoleModel = new UserRoleModel(user, role);

        return repository.save(userRoleModel);
    }

    @Override
    public Set<UserRoleModel> findByUser(Long userId) {
        return repository.getUserRoleModelsByUserId(userId);
    }
}
