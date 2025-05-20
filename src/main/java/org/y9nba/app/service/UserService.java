package org.y9nba.app.service;

import org.y9nba.app.constant.Role;
import org.y9nba.app.dto.search.UserSearchDto;
import org.y9nba.app.dto.user.*;
import org.y9nba.app.model.UserModel;

import java.util.Set;
import java.util.UUID;

public interface UserService {
    void saveWithManyRoles(UserCreateDto dto, Set<Role> role);
    void saveWithOneRole(UserCreateDto dto, Role role);
    void update(Long userId, UserUpdatePasswordDto dto);
    void update(Long userId, UserUpdateUsernameDto dto);
    String update(Long userId, UserUpdateDto dto);
    String tryUpdateEmail(Long userId, UserUpdateEmailDto dto);
    String updateEmail(Long userId, String updateEmailToken);
    String rollbackEmail(Long userId, String rollbackEmailToken);
    String activateUser(Long userId, String activateToken);
    String resetPassword(Long userId, UserResetPasswordDto dto, String resetPasswordToken);
    String rollbackPassword(Long userId, String rollbackPasswordToken);
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
