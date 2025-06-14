package org.y9nba.app.service.impl.admin;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.y9nba.app.constant.Role;
import org.y9nba.app.dao.entity.User;
import org.y9nba.app.dao.repository.UserRepository;
import org.y9nba.app.dto.user.UserCreateDto;
import org.y9nba.app.exception.local.IncorrectSuperAdminInitException;
import org.y9nba.app.exception.web.AbstractException;
import org.y9nba.app.service.face.admin.SuperAdminInitService;
import org.y9nba.app.service.impl.user.UserValidationServiceImpl;
import org.y9nba.app.util.PasswordUtil;

import java.util.Set;

@Service
@Slf4j
public class SuperAdminInitServiceImpl implements SuperAdminInitService {

    private final UserRepository repository;
    private final PasswordUtil passwordUtil;

    private final UserValidationServiceImpl userValidationService;

    public SuperAdminInitServiceImpl(UserRepository repository, PasswordUtil passwordUtil, UserValidationServiceImpl userValidationService) {
        this.repository = repository;
        this.passwordUtil = passwordUtil;
        this.userValidationService = userValidationService;
    }

    @Override
    public User createSuperAdmin(UserCreateDto dto) throws IncorrectSuperAdminInitException {
        String password = dto.getHashPassword();

        try {
            userValidationService.checkCreateUser(dto.getUsername(), dto.getEmail(), password);
        } catch (AbstractException e) {
            log.error("Произошла ошибка при создании супер админа: {}", e.getMessage());
            throw new IncorrectSuperAdminInitException();
        }

        dto.setHashPassword(passwordUtil.encode(dto.getHashPassword()));

        User model = new User(dto);

        model.setRole(Role.ROLE_SUPER_ADMIN);
        model.setEnabled(true);
        model.setStorageLimit(0L);

        return repository.save(model);
    }

    @Override
    public User getSuperAdmin() {
        Set<User> superAdmins = repository.findAllByRole(Role.ROLE_SUPER_ADMIN);

        if (superAdmins.size() > 1) {
            superAdmins
                    .stream()
                    .map(User::getId)
                    .forEach(repository::deleteById);
        } else if (superAdmins.size() == 1) {
            return superAdmins.stream().findFirst().orElse(null);
        }

        return null;
    }

    @Override
    public User updateSuperAdmin(User superAdmin, String username, String email, String password) throws IncorrectSuperAdminInitException {

        try {
            if (!superAdmin.getUsername().equals(username)) {
                userValidationService.checkUsername(username);
            }

            if (!superAdmin.getEmail().equals(email)) {
                userValidationService.checkEmail(email);
            }

            userValidationService.checkPassword(password);
        } catch (AbstractException e) {
            log.error("Произошла ошибка при обновлении данных супер админа: {}", e.getMessage());
            throw new IncorrectSuperAdminInitException();
        }

        superAdmin.setUsername(username);
        superAdmin.setEmail(email);
        superAdmin.setPassword(passwordUtil.encode(password));

        return repository.save(superAdmin);
    }
}
