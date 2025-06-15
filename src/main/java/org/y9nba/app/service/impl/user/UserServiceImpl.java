package org.y9nba.app.service.impl.user;

import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.y9nba.app.constant.Role;
import org.y9nba.app.dto.user.*;
import org.y9nba.app.dto.user.update.*;
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
    private final UserValidationServiceImpl userValidationService;

    public UserServiceImpl(UserRepository repository, PasswordUtil passwordUtil, AccountInfoServiceImpl accountInfoService, UserValidationServiceImpl userValidationService) {
        this.repository = repository;
        this.passwordUtil = passwordUtil;
        this.accountInfoService = accountInfoService;
        this.userValidationService = userValidationService;
    }

    @Override
    public User createUser(UserCreateDto dto) {
        String password = dto.getHashPassword();

        userValidationService.checkCreateUser(dto.getUsername(), dto.getEmail(), password);

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

        return save(model);
    }

    @Override
    public void update(Long userId, UserUpdatePasswordDto dto) {
        User model = getById(userId);

        userValidationService.checkUpdatePassword(dto.getOldPassword(), dto.getNewPassword(), model.getPassword());

        model.setPassword(passwordUtil.encode(dto.getNewPassword()));

        save(model);
    }

    @Override
    public void update(Long userId, UserUpdateUsernameDto dto) {
        User model = getById(userId);

        userValidationService.checkUpdateUsername(dto.getUsername(), model.getUsername());

        model.setUsername(dto.getUsername());

        save(model);
    }

    @Override
    public void update(Long userId, Long newUsedStorage) {
        User model = getById(userId);
        model.setUsedStorage(newUsedStorage);
        save(model);
    }

    @CacheEvict(value = {
            "UserSearchService::getUserById",
            "UserSearchService::getAdminById",
            "UserSearchService::getAllUsers",
            "UserSearchService::getAllActiveUsers",
            "UserSearchService::getAllBannedUsers",
            "UserSearchService::getAllAdmins",
            "UserService::getByUsername",
            "UserService::getByEmail",
            "UserService::getById"
    },
            allEntries = true
    )
    @Override
    public User save(User user) {
        return repository.save(user);
    }

    @CacheEvict(value = {
            "UserSearchService::getUserById",
            "UserSearchService::getAdminById",
            "UserSearchService::getAllUsers",
            "UserSearchService::getAllActiveUsers",
            "UserSearchService::getAllBannedUsers",
            "UserSearchService::getAllAdmins",
            "UserService::getByUsername",
            "UserService::getByEmail",
            "UserService::getById"
    },
            allEntries = true
    )
    @Override
    public void deleteById(Long id) {
        repository.deleteById(id);
    }

    @Cacheable(value = "UserService::getByUsername", key = "#username")
    @Override
    public User getByUsername(String username) {
        return repository
                .findByUsername(username)
                .orElseThrow(
                        () -> new NotFoundUserByUsernameException(username)
                );
    }

    @CacheEvict(value = "UserService::getByEmail", key = "#email")
    @Override
    public User getByEmail(String email) {
        return repository
                .findByEmail(email)
                .orElseThrow(
                        () -> new NotFoundUserByEmailException(email)
                );
    }

    @Cacheable(value = "UserService::getById", key = "#id")
    @Override
    public User getById(Long id) {
        return repository
                .findById(id)
                .orElseThrow(
                        () -> new NotFoundUserByIdException(id)
                );
    }
}
