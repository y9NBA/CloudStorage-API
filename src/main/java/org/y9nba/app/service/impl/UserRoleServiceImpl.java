package org.y9nba.app.service.impl;

import org.springframework.stereotype.Service;
import org.y9nba.app.dto.userrole.UserRoleCreateDto;
import org.y9nba.app.dto.userrole.UserRoleDto;
import org.y9nba.app.dto.userrole.UserRoleUpdateDto;
import org.y9nba.app.model.UserRoleModel;
import org.y9nba.app.repository.UserRoleRepository;
import org.y9nba.app.service.UserRoleService;

import java.util.Set;
import java.util.stream.Collectors;

@Service
public class UserRoleServiceImpl implements UserRoleService {

    private final UserRoleRepository repository;

    public UserRoleServiceImpl(UserRoleRepository repository) {
        this.repository = repository;
    }


    @Override
    public Set<UserRoleModel> saveAll(Set<UserRoleCreateDto> createDtoSet) {

        return createDtoSet.stream().map(this::save).collect(Collectors.toSet());
    }

    @Override
    public UserRoleModel save(UserRoleCreateDto entity) {
        UserRoleModel model = new UserRoleModel(entity);

        return repository.save(model);
    }

    @Override
    public UserRoleModel update(UserRoleUpdateDto entity) {
        return null;
    }

    @Override
    public void delete(UserRoleModel entity) {

    }

    @Override
    public void deleteById(UserRoleModel.UserRoleId id) {

    }

    @Override
    public UserRoleModel findById(UserRoleModel.UserRoleId id) {
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
