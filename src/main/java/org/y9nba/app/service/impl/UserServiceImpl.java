package org.y9nba.app.service.impl;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.y9nba.app.constant.Role;
import org.y9nba.app.dto.auth.RegistrationRequestDto;
import org.y9nba.app.dto.user.UserCreateDto;
import org.y9nba.app.dto.user.UserDto;
import org.y9nba.app.dto.user.UserUpdateDto;
import org.y9nba.app.dto.userrole.UserRoleCreateDto;
import org.y9nba.app.exception.NotFoundEntryException;
import org.y9nba.app.model.UserModel;
import org.y9nba.app.model.UserRoleModel;
import org.y9nba.app.repository.UserRepository;
import org.y9nba.app.security.JwtService;
import org.y9nba.app.service.UserService;

import java.util.Set;
import java.util.stream.Collectors;

@Service
public class UserServiceImpl implements UserService {

    private final UserRepository repository;

    private final UserRoleServiceImpl userRoleService;
    private final JwtService jwtService;

    public UserServiceImpl(UserRepository repository, UserRoleServiceImpl userRoleService, JwtService jwtService) {
        this.repository = repository;
        this.userRoleService = userRoleService;
        this.jwtService = jwtService;
    }

    @Override
    public UserDto saveWithManyRoles(UserCreateDto dto, Set<Role> roles) {
        UserModel model = repository.save(new UserModel(dto));

        Set<UserRoleModel> roleModels = userRoleService.saveAll(
                roles.stream().map(
                        e -> new UserRoleCreateDto(model, e)
                ).collect(Collectors.toSet())
        );

        model.setUserRoles(roleModels);

        return new UserDto(model);
    }

    @Override
    public UserDto saveWithOneRole(UserCreateDto dto, Role role) {
        UserModel model = repository.save(new UserModel(dto));
        UserRoleModel roleModel = userRoleService.save(new UserRoleCreateDto(model, role));

        model.setUserRoles(Set.of(roleModel));

        return new UserDto(model);
    }

    @Override
    public UserDto update(Long id, UserUpdateDto entity) {
        UserModel model = this.getById(id);

        model.setUsername(entity.getUsername());
        model.setEmail(entity.getEmail());
        model.setPassword(entity.getPassword());

        return new UserDto(repository.save(model));
    }

    @Override
    public boolean deleteById(Long id) {
        repository.deleteById(id);
        return !this.existsById(id);
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

    @Override
    public UserDto getUserByRequest(HttpServletRequest request) {
        String token = jwtService.getTokenByRequest(request);
        String username = jwtService.extractUsername(token);

        return new UserDto(getByUsername(username));
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return repository
                .findByUsername(username)
                .orElseThrow(
                        () -> new NotFoundEntryException("Not found user by username: " + username)
                );
    }
}
