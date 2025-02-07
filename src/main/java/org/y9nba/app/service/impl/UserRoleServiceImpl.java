package org.y9nba.app.service.impl;

import org.springframework.stereotype.Service;
import org.y9nba.app.constant.Role;
import org.y9nba.app.dto.user.UserDto;
import org.y9nba.app.dto.userrole.UserRoleCreateDto;
import org.y9nba.app.dto.userrole.UserRoleDto;
import org.y9nba.app.dto.userrole.UserRoleUpdateDto;
import org.y9nba.app.model.UserRoleModel;
import org.y9nba.app.repository.UserRoleRepository;
import org.y9nba.app.service.UserRoleService;

import java.util.Set;

@Service
public class UserRoleServiceImpl implements UserRoleService {

    private final UserRoleRepository repository;

    public UserRoleServiceImpl(UserRoleRepository repository) {
        this.repository = repository;
    }

    @Override
    public UserRoleDto save(UserDto user, Role role) {
        return null;
    }

    @Override
    public UserRoleDto save(UserRoleCreateDto entity) {
        return null;
    }

    @Override
    public UserRoleDto update(UserRoleUpdateDto entity) {
        return null;
    }

    @Override
    public void delete(UserRoleDto entity) {

    }

    @Override
    public void deleteById(UserRoleModel.UserRoleId id) {

    }

    @Override
    public UserRoleDto findById(UserRoleModel.UserRoleId id) {
        return null;
    }

    @Override
    public boolean existsById(UserRoleModel.UserRoleId id) {
        return false;
    }

    @Override
    public Set<UserRoleDto> findByUser(Long userId) {
        return null;
    }
}
