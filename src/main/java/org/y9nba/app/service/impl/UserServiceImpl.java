package org.y9nba.app.service.impl;

import org.springframework.stereotype.Service;
import org.y9nba.app.constant.Role;
import org.y9nba.app.dto.user.*;
import org.y9nba.app.dto.userrole.UserRoleCreateDto;
import org.y9nba.app.exception.*;
import org.y9nba.app.model.UserModel;
import org.y9nba.app.model.UserRoleModel;
import org.y9nba.app.repository.UserRepository;
import org.y9nba.app.security.JwtService;
import org.y9nba.app.service.UserService;
import org.y9nba.app.util.PasswordUtil;

import java.util.Set;
import java.util.stream.Collectors;

@Service
public class UserServiceImpl implements UserService {

    private final UserRepository repository;

    private final UserRoleServiceImpl userRoleService;
    private final JwtService jwtService;
    private final PasswordUtil passwordUtil;

    public UserServiceImpl(UserRepository repository, UserRoleServiceImpl userRoleService, JwtService jwtService, PasswordUtil passwordUtil) {
        this.repository = repository;
        this.userRoleService = userRoleService;
        this.jwtService = jwtService;
        this.passwordUtil = passwordUtil;
    }

    @Override
    public void saveWithManyRoles(UserCreateDto dto, Set<Role> roles) {
        UserModel model = repository.save(new UserModel(dto));

        Set<UserRoleModel> roleModels = userRoleService.saveAll(
                roles.stream().map(
                        e -> new UserRoleCreateDto(model, e)
                ).collect(Collectors.toSet())
        );
    }

    @Override
    public void saveWithOneRole(UserCreateDto dto, Role role) {
        UserModel model = repository.save(new UserModel(dto));
        userRoleService.save(new UserRoleCreateDto(model, role));
    }

    @Override
    public void update(Long userId, UserUpdatePasswordDto dto) {
        UserModel model = getById(userId);

        if(!passwordUtil.matches(dto.getOldPassword(), model.getPassword())) {
            throw new PasswordIncorrectException();
        }

        if(!passwordUtil.matches(dto.getNewPassword(), model.getPassword())) {
            throw new PasswordDuplicateException();
        }

        model.setPassword(passwordUtil.encode(dto.getNewPassword()));

        repository.save(model);
    }

    @Override
    public void update(Long userId, UserUpdateEmailDto dto) {
        UserModel model = getById(userId);

        if(dto.getEmail().equals(model.getEmail())) {
            throw new EmailDuplicateException();
        }

        if(existsByEmail(dto.getEmail())) {
            throw new EmailAlreadyException();
        }

        model.setEmail(dto.getEmail());

        repository.save(model);
    }

    @Override
    public void update(Long userId, UserUpdateUsernameDto dto) {
        UserModel model = getById(userId);

        if(dto.getUsername().equals(model.getUsername())) {
            throw new UsernameDuplicateException();
        }

        if(existsByUsername(dto.getUsername())) {
            throw new UsernameAlreadyException();
        }

        model.setUsername(dto.getUsername());

        repository.save(model);
    }

    @Override
    public void update(Long userId, UserUpdateDto dto) {
        update(userId, new UserUpdateEmailDto(dto.getEmail()));
        update(userId, new UserUpdatePasswordDto(dto.getOldPassword(), dto.getNewPassword()));
        update(userId, new UserUpdateUsernameDto(dto.getUsername()));
    }

    @Override
    public boolean deleteById(Long id) {
        repository.deleteById(id);
        return !this.existsById(id);
    }

    @Override
    public boolean deleteByUsername(String username) {
        repository.delete(this.getByUsername(username));
        return !this.existsByUsername(username);
    }

    @Override
    public UserModel getByUsername(String username) {
        return repository
                .findByUsername(username)
                .orElseThrow(
                        () -> new NotFoundEntryException("Not found user by username: " + username)
                );
    }

    @Override
    public UserModel getByEmail(String email) {
        return repository
                .findByEmail(email)
                .orElseThrow(
                        () -> new NotFoundEntryException("Not found user by email: " + email)
                );
    }

    @Override
    public UserModel getById(Long id) {
        return repository
                .findById(id)
                .orElseThrow(
                        () -> new NotFoundEntryException("Not found user by id: " + id)
                );
    }

    @Override
    public boolean existsByUsername(String username) {
        return repository.existsByUsername(username);
    }

    @Override
    public boolean existsByEmail(String email) {
        return repository.existsByEmail(email);
    }

    @Override
    public boolean existsById(Long id) {
        return repository.existsById(id);
    }
}
