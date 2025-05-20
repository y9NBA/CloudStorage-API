package org.y9nba.app.service;

import org.y9nba.app.constant.Role;
import org.y9nba.app.dto.userrole.UserRoleCreateDto;
import org.y9nba.app.dto.userrole.UserRoleDto;
import org.y9nba.app.dto.userrole.UserRoleUpdateDto;
import org.y9nba.app.model.UserRoleModel;

import java.util.Set;

public interface UserRoleService {
    Set<UserRoleModel> saveAll(Set<UserRoleCreateDto> createDtoSet);
    UserRoleModel save(UserRoleCreateDto entity);
    UserRoleModel update(UserRoleUpdateDto entity);
    void delete(UserRoleModel entity);
    void deleteById(UserRoleModel.UserRoleId id);
    UserRoleModel findById(UserRoleModel.UserRoleId id);
    boolean existsById(UserRoleModel.UserRoleId id);
    Set<UserRoleDto> findByUser(Long userId);
    Set<UserRoleModel> getAllUsersByRole(Role role);
}
