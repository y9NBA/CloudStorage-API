package org.y9nba.app.service;

import jakarta.servlet.http.HttpServletRequest;
import org.y9nba.app.constant.Role;
import org.y9nba.app.dto.user.*;
import org.y9nba.app.model.UserModel;

import java.util.Set;

public interface UserService {
    void saveWithManyRoles(UserCreateDto dto, Set<Role> role);
    void saveWithOneRole(UserCreateDto dto, Role role);
    void update(String username, UserUpdatePasswordDto dto);
    void update(String username, UserUpdateEmailDto dto);
    void update(String username, UserUpdateUsernameDto dto);
    void update(String username, UserUpdateDto dto);
    boolean deleteById(Long id);
    boolean deleteByUsername(String username);
    UserModel getByUsername(String username);
    UserModel getByEmail(String email);
    UserModel getById(Long id);
    boolean existsByUsername(String username);
    boolean existsByEmail(String email);
    boolean existsById(Long id);
    UserDto getUserByRequest(HttpServletRequest request);
}
