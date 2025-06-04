package org.y9nba.app.service.impl.user;

import org.springframework.stereotype.Service;
import org.y9nba.app.dao.entity.User;
import org.y9nba.app.dto.user.update.UserResetPasswordDto;
import org.y9nba.app.exception.web.auth.AccountLockedException;
import org.y9nba.app.exception.web.user.info.PasswordDuplicateException;
import org.y9nba.app.security.JwtService;
import org.y9nba.app.service.face.user.UserPasswordService;
import org.y9nba.app.service.impl.email.ConfirmServiceImpl;
import org.y9nba.app.util.PasswordUtil;

@Service
public class UserPasswordServiceImpl implements UserPasswordService {

    private final UserServiceImpl userService;
    private final ConfirmServiceImpl confirmService;
    private final PasswordUtil passwordUtil;
    private final JwtService jwtService;

    public UserPasswordServiceImpl(UserServiceImpl userService, ConfirmServiceImpl confirmService, PasswordUtil passwordUtil, JwtService jwtService) {
        this.userService = userService;
        this.confirmService = confirmService;
        this.passwordUtil = passwordUtil;
        this.jwtService = jwtService;
    }

    @Override
    public String resetPasswordByEmail(String email) {
        User model = userService.getByEmail(email);

        if (model.isBanned()) {
            throw new AccountLockedException();
        }

        return confirmService.sendResetPasswordConfirmation(model);
    }

    @Override
    public String resetPassword(Long userId, UserResetPasswordDto dto, String resetPasswordToken) {
        confirmService.confirmResetPassword(userId, resetPasswordToken);

        User model = userService.getById(userId);
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
        userService.save(model);

        jwtService.revokeAllSession(userId);

        return res;
    }

    @Override
    public String rollbackPassword(Long userId, String rollbackPasswordToken) {
        String oldHashPassword = confirmService.confirmRollbackPassword(userId, rollbackPasswordToken);
        User model = userService.getById(userId);

        model.setPassword(oldHashPassword);
        userService.save(model);

        jwtService.revokeAllSession(userId);

        return "Смена пароля отменена";
    }
}
