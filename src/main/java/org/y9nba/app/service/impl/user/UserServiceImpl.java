package org.y9nba.app.service.impl.user;

import org.springframework.stereotype.Service;
import org.y9nba.app.constant.Role;
import org.y9nba.app.dto.user.*;
import org.y9nba.app.dto.user.update.*;
import org.y9nba.app.exception.web.user.info.*;
import org.y9nba.app.exception.web.user.search.NotFoundUserByEmailException;
import org.y9nba.app.exception.web.user.search.NotFoundUserByIdException;
import org.y9nba.app.exception.web.user.search.NotFoundUserByUsernameException;
import org.y9nba.app.dao.entity.User;
import org.y9nba.app.dao.repository.UserRepository;
import org.y9nba.app.service.face.user.UserService;
import org.y9nba.app.service.impl.email.AccountInfoServiceImpl;
import org.y9nba.app.util.PasswordUtil;

@Service
public class UserServiceImpl implements UserService {

    private final UserRepository repository;
    private final PasswordUtil passwordUtil;

    private final AccountInfoServiceImpl accountInfoService;

    public UserServiceImpl(UserRepository repository, PasswordUtil passwordUtil, AccountInfoServiceImpl accountInfoService) {
        this.repository = repository;
        this.passwordUtil = passwordUtil;
        this.accountInfoService = accountInfoService;
    }

    @Override
    public User createUser(UserCreateDto dto) {
        String password = dto.getHashPassword();

        dto.setHashPassword(passwordUtil.encode(password));

        User model = new User(dto);

        model.setRole(Role.ROLE_USER);

        if (dto.isOauth2()) {
            model.setEnabled(true);

            accountInfoService.sendUserOAuth2AccountInfo(
                    model.getEmail(),
                    model.getUsername(),
                    password
            );
        }

        return repository.save(model);
    }

    @Override
    public void update(Long userId, UserUpdatePasswordDto dto) {
        User model = getById(userId);

        if (!passwordUtil.matches(dto.getOldPassword(), model.getPassword())) {
            throw new PasswordIncorrectException();
        }

        if (passwordUtil.matches(dto.getNewPassword(), model.getPassword())) {
            throw new PasswordDuplicateException();
        }

        model.setPassword(passwordUtil.encode(dto.getNewPassword()));

        repository.save(model);
    }

    @Override
    public void update(Long userId, UserUpdateUsernameDto dto) {
        User model = getById(userId);

        if (dto.getUsername().equals(model.getUsername())) {
            throw new UsernameDuplicateException();
        }

        if (existsByUsername(dto.getUsername())) {
            throw new UsernameAlreadyException();
        }

        model.setUsername(dto.getUsername());

        repository.save(model);
    }

    @Override
    public void update(Long userId, Long newUsedStorage) {
        User model = getById(userId);
        model.setUsedStorage(newUsedStorage);
        repository.save(model);
    }

    @Override
    public User save(User user) {
        return repository.save(user);
    }

    @Override
    public void deleteById(Long id) {
        repository.deleteById(id);
    }

    @Override
    public User getByUsername(String username) {
        return repository
                .findByUsername(username)
                .orElseThrow(
                        () -> new NotFoundUserByUsernameException(username)
                );
    }

    @Override
    public User getByEmail(String email) {
        return repository
                .findByEmail(email)
                .orElseThrow(
                        () -> new NotFoundUserByEmailException(email)
                );
    }

    @Override
    public User getById(Long id) {
        return repository
                .findById(id)
                .orElseThrow(
                        () -> new NotFoundUserByIdException(id)
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
}
