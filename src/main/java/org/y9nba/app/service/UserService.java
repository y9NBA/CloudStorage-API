package org.y9nba.app.service;

import org.y9nba.app.constant.Role;
import org.y9nba.app.dto.search.UserSearchDto;
import org.y9nba.app.dto.user.*;
import org.y9nba.app.model.UserModel;

import java.util.Set;

public interface UserService {
    void saveWithManyRoles(UserCreateDto dto, Set<Role> role);
    void saveWithOneRole(UserCreateDto dto, Role role);
    void update(Long userId, UserUpdatePasswordDto dto);
    void update(Long userId, UserUpdateEmailDto dto);
    void update(Long userId, UserUpdateUsernameDto dto);
    void update(Long userId, UserUpdateDto dto);
    void update(Long userId, Long newUsedStorage);
    boolean deleteById(Long id);
    boolean deleteByUsername(String username);
    UserModel getByUsername(String username);
    UserModel getByEmail(String email);
    UserModel getById(Long id);
    boolean existsByUsername(String username);
    boolean existsByEmail(String email);
    boolean existsById(Long id);
    Set<UserSearchDto> getAllUsers(Long userId);
}
