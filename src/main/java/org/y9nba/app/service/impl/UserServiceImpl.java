package org.y9nba.app.service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.y9nba.app.dto.user.UserCreateDto;
import org.y9nba.app.dto.user.UserDto;
import org.y9nba.app.dto.user.UserUpdateDto;
import org.y9nba.app.model.UserModel;
import org.y9nba.app.repository.UserRepository;
import org.y9nba.app.service.UserService;

@Service
public class UserServiceImpl implements UserService {

    private final UserRepository repository;
    private final ObjectMapper mapper = new ObjectMapper();

    public UserServiceImpl(UserRepository repository) {
        this.repository = repository;
    }

    @Override
    public UserDto save(UserCreateDto entity) {
        return null;
    }

    @Override
    public UserDto update(UserUpdateDto entity) {
        return null;
    }

    @Override
    public void delete(UserDto entity) {

    }

    @Override
    public void deleteById(Long id) {

    }

    @Override
    public UserDto findById(Long id) {
        return new UserDto(
                repository
                .findById(id)
                .orElseThrow(
                        () -> new HttpClientErrorException(HttpStatus.BAD_REQUEST)
                )
        );
    }

    @Override
    public boolean existsById(Long id) {
        return false;
    }
}
