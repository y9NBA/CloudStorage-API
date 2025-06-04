package org.y9nba.app.service.impl.user;

import org.springframework.stereotype.Service;
import org.y9nba.app.dao.entity.User;
import org.y9nba.app.exception.web.auth.AccountLockedException;
import org.y9nba.app.exception.web.user.info.ActiveAlreadyException;
import org.y9nba.app.service.face.user.UserActivationService;
import org.y9nba.app.service.impl.email.ConfirmServiceImpl;

@Service
public class UserActivationServiceImpl implements UserActivationService {

    private final UserServiceImpl userService;
    private final ConfirmServiceImpl confirmService;

    public UserActivationServiceImpl(UserServiceImpl userService, ConfirmServiceImpl confirmService) {
        this.userService = userService;
        this.confirmService = confirmService;
    }

    @Override
    public String resendActivationByEmail(String email) {
        User model = userService.getByEmail(email);

        if (model.isEnabled()) {
            throw new ActiveAlreadyException();
        }

        if (model.isBanned()) {
            throw new AccountLockedException();
        }

        return confirmService.sendActivateAccountConfirmation(model);
    }

    @Override
    public String activateUser(Long userId, String activateToken) {
        confirmService.confirmActivateAccount(userId, activateToken);

        User model = userService.getById(userId);
        model.setEnabled(true);
        userService.save(model);

        return "Аккаунт успешно активирован";
    }
}
