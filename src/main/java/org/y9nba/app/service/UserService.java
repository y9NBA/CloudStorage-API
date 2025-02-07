package org.y9nba.app.service;

import org.y9nba.app.dto.user.UserCreateDto;
import org.y9nba.app.dto.user.UserDto;
import org.y9nba.app.dto.user.UserUpdateDto;

public interface UserService {
    UserDto save(UserCreateDto entity);
    UserDto update(UserUpdateDto entity);
    void delete(UserDto entity);
    void deleteById(Long id);
    UserDto findById(Long id);
    boolean existsById(Long id);
}
