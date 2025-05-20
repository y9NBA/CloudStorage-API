package org.y9nba.app.service.impl;

import org.springframework.stereotype.Service;
import org.y9nba.app.constant.Role;
import org.y9nba.app.dto.search.UserSearchDto;
import org.y9nba.app.dto.user.*;
import org.y9nba.app.dto.userrole.UserRoleCreateDto;
import org.y9nba.app.exception.web.user.info.*;
import org.y9nba.app.exception.web.user.search.NotFoundUserByEmailException;
import org.y9nba.app.exception.web.user.search.NotFoundUserByIdException;
import org.y9nba.app.exception.web.user.search.NotFoundUserByUsernameException;
import org.y9nba.app.model.UserModel;
import org.y9nba.app.model.UserRoleModel;
import org.y9nba.app.repository.UserRepository;
import org.y9nba.app.security.JwtService;
import org.y9nba.app.service.UserService;
import org.y9nba.app.util.PasswordUtil;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class UserServiceImpl implements UserService {

    private final UserRepository repository;

    private final UserRoleServiceImpl userRoleService;
    private final ConfirmServiceImpl confirmService;
    private final JwtService jwtService;
    private final PasswordUtil passwordUtil;

    public UserServiceImpl(UserRepository repository, UserRoleServiceImpl userRoleService, ConfirmServiceImpl confirmService, JwtService jwtService, PasswordUtil passwordUtil) {
        this.repository = repository;
        this.userRoleService = userRoleService;
        this.confirmService = confirmService;
        this.jwtService = jwtService;
        this.passwordUtil = passwordUtil;
    }

    @Override
    public void saveWithManyRoles(UserCreateDto dto, Set<Role> roles) {
        UserModel model = repository.save(new UserModel(dto));

        Set<UserRoleModel> roleModels = userRoleService.saveAll(
                roles.stream().map(
                        e -> new UserRoleCreateDto(model, e)
                ).collect(Collectors.toSet())
        );
    }

    @Override
    public void saveWithOneRole(UserCreateDto dto, Role role) {
        UserModel model = repository.save(new UserModel(dto));
        userRoleService.save(new UserRoleCreateDto(model, role));
    }

    @Override
    public void update(Long userId, UserUpdatePasswordDto dto) {
        UserModel model = getById(userId);

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
    public String updateEmail(Long userId, String updateEmailToken) {
        String newEmail = confirmService.confirmEmailChange(userId, updateEmailToken);
        String oldEmail = getById(userId).getEmail();

        updateUserEmail(userId, newEmail);

        confirmService.sendRollbackUpdateEmailConfirmation(getById(userId), oldEmail);

        return "Email успешно изменен на: " + newEmail;
    }

    @Override
    public String rollbackEmail(Long userId, String rollbackEmailToken) {
        String oldEmail = confirmService.confirmRollbackEmail(userId, rollbackEmailToken);

        updateUserEmail(userId, oldEmail);

        jwtService.revokeAllToken(getById(userId));

        return "Изменение почты отменено. Почта осталась: " + oldEmail;
    }

    private void updateUserEmail(Long userId, String email) {
        UserModel model = getById(userId);

        checkEmail(model.getEmail(), email);

        model.setEmail(email);
        repository.save(model);
    }

    @Override
    public String activateUser(Long userId, String activateToken) {
        confirmService.confirmActivateAccount(userId, activateToken);

        UserModel model = getById(userId);

        model.setEnabled(true);
        repository.save(model);

        return "Аккаунт успешно активирован";
    }

    @Override
    public String resetPassword(Long userId, UserResetPasswordDto dto, String resetPasswordToken) {
        confirmService.confirmResetPassword(userId, resetPasswordToken);

        UserModel model = getById(userId);
        String encodedPassword;
        String res;

        if (dto != null) {
            if (passwordUtil.matches(dto.getNewPassword(), model.getPassword())) {
                throw new PasswordDuplicateException();
            } else {
                encodedPassword = passwordUtil.encode(dto.getNewPassword());
                res = confirmService.sendResetPasswordInformation(model);
            }
        } else {
            String password = passwordUtil.generateRandomPassword(10L);
            encodedPassword = passwordUtil.encode(password);
            res = confirmService.sendResetPasswordInformation(model, password);
        }

        model.setPassword(encodedPassword);
        repository.save(model);

        jwtService.revokeAllToken(model);

        return res;
    }

    @Override
    public String rollbackPassword(Long userId, String rollbackPasswordToken) {
        String oldHashPassword = confirmService.confirmRollbackPassword(userId, rollbackPasswordToken);
        UserModel model = getById(userId);

        model.setPassword(oldHashPassword);
        repository.save(model);

        jwtService.revokeAllToken(model);

        return "Смена пароля отменена";
    }

    @Override
    public String tryUpdateEmail(Long userId, UserUpdateEmailDto dto) {
        UserModel model = getById(userId);

        checkEmail(model.getEmail(), dto.getEmail());

        return confirmService.sendUpdateEmailConfirmation(model, dto.getEmail());
    }

    public void checkEmail(String email, String newEmail) {
        if (newEmail.equals(email)) {
            throw new EmailDuplicateException();
        }

        if (existsByEmail(newEmail)) {
            throw new EmailAlreadyException();
        }
    }

    @Override
    public void update(Long userId, UserUpdateUsernameDto dto) {
        UserModel model = getById(userId);

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
    public String update(Long userId, UserUpdateDto dto) {
        update(userId, new UserUpdatePasswordDto(dto.getOldPassword(), dto.getNewPassword()));
        update(userId, new UserUpdateUsernameDto(dto.getUsername()));

        return tryUpdateEmail(userId, new UserUpdateEmailDto(dto.getEmail()));
    }

    @Override
    public void update(Long userId, Long newUsedStorage) {
        UserModel model = getById(userId);
        model.setUsedStorage(newUsedStorage);

        repository.save(model);
    }

    @Override
    public boolean deleteById(Long id) {
        repository.deleteById(id);
        return !this.existsById(id);
    }

    @Override
    public boolean deleteByUsername(String username) {
        repository.delete(this.getByUsername(username));
        return !this.existsByUsername(username);
    }

    @Override
    public UserModel getByUsername(String username) {
        return repository
                .findByUsername(username)
                .orElseThrow(
                        () -> new NotFoundUserByUsernameException(username)
                );
    }

    @Override
    public UserModel getByEmail(String email) {
        return repository
                .findByEmail(email)
                .orElseThrow(
                        () -> new NotFoundUserByEmailException(email)
                );
    }

    @Override
    public UserModel getById(Long id) {
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

    @Override
    public boolean existsById(Long id) {
        return repository.existsById(id);
    }

    @Override
    public String resendActivationByEmail(String email) {
        UserModel model = getByEmail(email);

        if (model.isEnabled()) {
            throw new ActiveAlreadyException();
        }

        return confirmService.sendActivateAccountConfirmation(model);
    }

    @Override
    public String resetPasswordByEmail(String email) {
        UserModel model = getByEmail(email);

        return confirmService.sendResetPasswordConfirmation(model);
    }

    @Override
    public Set<UserSearchDto> getAllUsers(String username, String email, UUID bucketName, Long userId) {
        Set<UserModel> models = new HashSet<>(repository.findAll());

        if (username != null) {
            models = models
                    .stream()
                    .filter(
                            u -> u.getUsername()
                                    .toLowerCase()
                                    .contains(username.toLowerCase())
                    )
                    .collect(Collectors.toSet());
        }

        if (email != null) {
            models = models
                    .stream()
                    .filter(
                            u -> u.getEmail()
                                    .toLowerCase()
                                    .contains(email.toLowerCase())
                    )
                    .collect(Collectors.toSet());
        }

        if (bucketName != null) {
            models = models
                    .stream()
                    .filter(
                            u -> u.getBucketName().equals(bucketName.toString())
                    )
                    .collect(Collectors.toSet());
        }

        return models
                .stream()
                .filter(u -> !u.getId().equals(userId))
                .map(UserSearchDto::new)
                .collect(Collectors.toSet());
    }
}
