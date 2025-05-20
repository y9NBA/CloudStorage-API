package org.y9nba.app.service;

import org.y9nba.app.dto.search.UserSearchDto;
import org.y9nba.app.dto.user.*;
import org.y9nba.app.model.UserModel;

import java.util.Set;
import java.util.UUID;

public interface UserService {
    UserModel createSuperAdmin(UserCreateDto dto);
    UserModel createAdmin(UserCreateDto dto);
    UserModel createUser(UserCreateDto dto);
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
    String resendActivationByEmail(String email);
    String resetPasswordByEmail(String email);
    Set<UserSearchDto> getAllUsers(String username, String email, UUID bucketName, Long userId);
    UserModel getSuperAdmin();
    UserModel updateSuperAdmin(UserModel superAdminWithUpdates);
}
