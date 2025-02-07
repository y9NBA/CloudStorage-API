package org.y9nba.app.service;

import org.y9nba.app.constant.Role;
import org.y9nba.app.dto.user.UserDto;
import org.y9nba.app.dto.user.UserUpdateDto;
import org.y9nba.app.dto.userrole.UserRoleCreateDto;
import org.y9nba.app.dto.userrole.UserRoleDto;
import org.y9nba.app.dto.userrole.UserRoleUpdateDto;
import org.y9nba.app.model.UserRoleModel;

import java.util.Set;

public interface UserRoleService {
    UserRoleDto save(UserDto user, Role role);
    UserRoleDto save(UserRoleCreateDto entity);
    UserRoleDto update(UserRoleUpdateDto entity);
    void delete(UserRoleDto entity);
    void deleteById(UserRoleModel.UserRoleId id);
    UserRoleDto findById(UserRoleModel.UserRoleId id);
    boolean existsById(UserRoleModel.UserRoleId id);
    Set<UserRoleDto> findByUser(Long userId);
}
