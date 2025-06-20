package org.y9nba.app.service.impl.user;

import org.springframework.stereotype.Service;
import org.y9nba.app.dao.entity.User;
import org.y9nba.app.dto.user.update.UserUpdateEmailDto;
import org.y9nba.app.security.jwt.JwtService;
import org.y9nba.app.service.face.user.UserEmailService;
import org.y9nba.app.service.impl.email.ConfirmServiceImpl;

@Service
public class UserEmailServiceImpl implements UserEmailService {

    private final UserServiceImpl userService;
    private final ConfirmServiceImpl confirmService;
    private final JwtService jwtService;
    private final UserValidationServiceImpl userValidationService;

    public UserEmailServiceImpl(UserServiceImpl userService, ConfirmServiceImpl confirmService, JwtService jwtService, UserValidationServiceImpl userValidationService) {
        this.userService = userService;
        this.confirmService = confirmService;
        this.jwtService = jwtService;
        this.userValidationService = userValidationService;
    }

    @Override
    public String tryUpdateEmail(Long userId, UserUpdateEmailDto dto) {
        User model = userService.getById(userId);

        userValidationService.checkUpdateEmail(model.getEmail(), dto.getEmail());

        return confirmService.sendUpdateEmailConfirmation(model, dto.getEmail());
    }

    @Override
    public String updateEmail(Long userId, String updateEmailToken) {
        String newEmail = confirmService.confirmEmailChange(userId, updateEmailToken);
        String oldEmail = userService.getById(userId).getEmail();

        updateUserEmail(userId, newEmail);

        confirmService.sendRollbackUpdateEmailConfirmation(userService.getById(userId), oldEmail);

        return "Email успешно изменен на: " + newEmail;
    }

    @Override
    public String rollbackEmail(Long userId, String rollbackEmailToken) {
        String oldEmail = confirmService.confirmRollbackEmail(userId, rollbackEmailToken);

        updateUserEmail(userId, oldEmail);

        jwtService.revokeAllSession(userId);

        return "Изменение почты отменено. Почта осталась: " + oldEmail;
    }

    private void updateUserEmail(Long userId, String email) {
        User model = userService.getById(userId);

        userValidationService.checkUpdateEmail(model.getEmail(), email);

        model.setEmail(email);

        userService.save(model);
    }
}
