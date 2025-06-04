package org.y9nba.app.service.face.user;

import org.y9nba.app.dao.entity.User;
import org.y9nba.app.dto.user.UserCreateDto;
import org.y9nba.app.dto.user.update.UserUpdatePasswordDto;
import org.y9nba.app.dto.user.update.UserUpdateUsernameDto;

public interface UserService {
    User createUser(UserCreateDto dto);
    void update(Long userId, UserUpdatePasswordDto dto);
    void update(Long userId, UserUpdateUsernameDto dto);
    void update(Long userId, Long newUsedStorage);
    User save(User user);
    void deleteById(Long id);
    User getByUsername(String username);
    User getByEmail(String email);
    User getById(Long id);
    boolean existsByUsername(String username);
    boolean existsByEmail(String email);
}

