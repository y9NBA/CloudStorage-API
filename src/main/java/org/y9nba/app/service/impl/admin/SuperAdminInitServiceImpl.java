package org.y9nba.app.service.impl.admin;

import org.springframework.stereotype.Service;
import org.y9nba.app.constant.Role;
import org.y9nba.app.dao.entity.User;
import org.y9nba.app.dao.repository.UserRepository;
import org.y9nba.app.dto.user.UserCreateDto;
import org.y9nba.app.service.face.admin.SuperAdminInitService;
import org.y9nba.app.util.PasswordUtil;

import java.util.Set;

@Service
public class SuperAdminInitServiceImpl implements SuperAdminInitService {

    private final UserRepository repository;
    private final PasswordUtil passwordUtil;

    public SuperAdminInitServiceImpl(UserRepository repository, PasswordUtil passwordUtil) {
        this.repository = repository;
        this.passwordUtil = passwordUtil;
    }

    @Override
    public User createSuperAdmin(UserCreateDto dto) {
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
    public User updateSuperAdmin(User superAdminWithUpdates) {
        return repository.save(superAdminWithUpdates);
    }
}
