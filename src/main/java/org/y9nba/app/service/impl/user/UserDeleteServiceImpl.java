package org.y9nba.app.service.impl.user;

import org.springframework.stereotype.Service;
import org.y9nba.app.constant.Role;
import org.y9nba.app.dao.entity.User;
import org.y9nba.app.service.face.user.UserDeleteService;
import org.y9nba.app.service.impl.email.ConfirmServiceImpl;
import org.y9nba.app.service.impl.file.FileStorageServiceImpl;

@Service
public class UserDeleteServiceImpl implements UserDeleteService {

    private final UserServiceImpl userService;
    private final ConfirmServiceImpl confirmService;
    private final FileStorageServiceImpl fileStorageService;

    public UserDeleteServiceImpl(UserServiceImpl userService, ConfirmServiceImpl confirmService, FileStorageServiceImpl fileStorageService) {
        this.userService = userService;
        this.confirmService = confirmService;
        this.fileStorageService = fileStorageService;
    }

    @Override
    public String deleteUserByEmail(String email) {
        User user = userService.getByEmail(email);
        return confirmService.sendAccountDeleteConfirmation(user);
    }

    @Override
    public String deleteUser(Long userId, String deleteAccountToken) {
        confirmService.confirmDeleteAccount(userId, deleteAccountToken);

        User user = userService.getById(userId);

        if (user.getRole().equals(Role.ROLE_USER)) {
            fileStorageService.deleteAllFilesByDeletedUserId(userId);
        }

        userService.deleteById(user.getId());

        return "Аккаунт и все его данные были удалены. Прощайте ;)";
    }
}
