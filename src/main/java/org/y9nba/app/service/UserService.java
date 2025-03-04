package org.y9nba.app.service;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.y9nba.app.constant.Role;
import org.y9nba.app.dto.auth.RegistrationRequestDto;
import org.y9nba.app.dto.user.UserCreateDto;
import org.y9nba.app.dto.user.UserDto;
import org.y9nba.app.dto.user.UserUpdateDto;
import org.y9nba.app.model.UserModel;

import java.util.Set;

public interface UserService extends UserDetailsService {
    UserDto saveWithManyRoles(UserCreateDto dto, Set<Role> role);
    UserDto saveWithOneRole(UserCreateDto dto, Role role);
    UserDto update(Long id, UserUpdateDto dto);
    boolean deleteById(Long id);
    UserModel getByUsername(String username);
    UserModel getByEmail(String email);
    UserModel getById(Long id);
    boolean existsByUsername(String username);
    boolean existsByEmail(String email);
    boolean existsById(Long id);
    UserDto getUserByRequest(HttpServletRequest request);
}
