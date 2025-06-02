package org.y9nba.app.service.impl.email;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.y9nba.app.constant.OneTimeTokenType;
import org.y9nba.app.dao.entity.User;
import org.y9nba.app.service.face.email.ConfirmService;
import org.y9nba.app.service.impl.token.OneTimeTokenServiceImpl;

import java.util.UUID;

@Service
public class ConfirmServiceImpl implements ConfirmService {


    @Value("${domain.url}")
    private String domainURL;

    private final OneTimeTokenServiceImpl oneTimeTokenService;
    private final EmailServiceImpl emailService;

    public ConfirmServiceImpl(OneTimeTokenServiceImpl oneTimeTokenService, EmailServiceImpl emailService) {
        this.oneTimeTokenService = oneTimeTokenService;
        this.emailService = emailService;
    }

    @Override
    public String sendUpdateEmailConfirmation(User user, String newEmail) {
        String token = oneTimeTokenService.createUpdateEmailToken(user, newEmail);

        emailService.sendUpdateEmailConfirmationMessage(
                newEmail,
                domainURL + "confirm/update-email/token?key=" + token
        );

        return "Письмо с подтверждением смены почты отправлено на: " + newEmail;
    }

    @Override
    public void sendRollbackUpdateEmailConfirmation(User user, String oldEmail) {
        String token = oneTimeTokenService.createRollbackEmailToken(user, oldEmail);

        emailService.sendRollbackUpdateEmailConfirmationMessage(
                oldEmail,
                domainURL + "confirm/rollback-email/token?key=" + token
        );
    }

    @Override
    public String sendActivateAccountConfirmation(User user) {
        String token = oneTimeTokenService.createActivationToken(user);

        emailService.sendActivationAccountConfirmationMessage(
                user.getEmail(),
                domainURL + "confirm/activate/token?key=" + token
        );

        return "Письмо с подтверждением регистрации отправлено на: " + user.getEmail() + " :)";
    }

    @Override
    public String sendResetPasswordConfirmation(User user) {
        String token = oneTimeTokenService.createResetPasswordToken(user);

        emailService.sendResetPasswordConfirmationMessage(
                user.getEmail(),
                domainURL + "confirm/reset-password/token?key=" + token
        );

        return "Письмо с инструкцией по сбросу пароля отправлено на: " + user.getEmail();
    }

    @Override
    public String sendAccountDeleteConfirmation(User user) {
        String token = oneTimeTokenService.createDeleteAccountToken(user);

        emailService.sendDeleteAccountConfirmationMessage(
                user.getEmail(),
                domainURL + "confirm/delete-account/token?key=" + token
        );

        return "Письмо с подтверждением удаления аккаунта отправлено на: " + user.getEmail();
    }

    @Override
    public String sendResetPasswordInformation(User user, String newPassword) {
        String token = oneTimeTokenService.createRollbackPasswordToken(user);

        emailService.sendResetPasswordInfoMessage(
                user.getEmail(),
                user.getUsername(),
                newPassword,
                domainURL + "confirm/rollback-password/token?key=" + token

        );

        return "Информационное письмо с новым паролем отправлено на: " + user.getEmail();
    }

    @Override
    public String sendResetPasswordInformation(User user) {
        String token = oneTimeTokenService.createRollbackPasswordToken(user);

        emailService.sendResetPasswordInfoMessage(
                user.getEmail(),
                domainURL + "confirm/rollback-password/token?key=" + token

        );

        return "Пароль успешно изменён";
    }

    @Override
    public void confirmActivateAccount(Long userId, String activationToken) {
        checkAndRevokeAllOneTimeTokenWithType(userId, activationToken, OneTimeTokenType.ACTIVATION);
    }

    @Override
    public void confirmResetPassword(Long userId, String resetPasswordToken) {
        checkAndRevokeAllOneTimeTokenWithType(userId, resetPasswordToken, OneTimeTokenType.RESET_PASSWORD);
    }

    @Override
    public String confirmRollbackPassword(Long userId, String rollbackPasswordToken) {
        checkAndRevokeAllOneTimeTokenWithType(userId, rollbackPasswordToken, OneTimeTokenType.ROLLBACK_PASSWORD);
        return oneTimeTokenService.extractHashPasswordFromToken(rollbackPasswordToken);
    }

    @Override
    public String confirmRollbackEmail(Long userId, String rollbackEmailToken) {
        checkAndRevokeAllOneTimeTokenWithType(userId, rollbackEmailToken, OneTimeTokenType.ROLLBACK_EMAIL);
        return oneTimeTokenService.extractEmailFromToken(rollbackEmailToken);
    }

    @Override
    public void confirmDeleteAccount(Long userId, String deleteAccountToken) {
        checkAndRevokeAllOneTimeTokenWithType(userId, deleteAccountToken, OneTimeTokenType.DELETE_ACCOUNT);
    }

    @Override
    public String confirmEmailChange(Long userId, String updateEmailToken) {
        checkAndRevokeAllOneTimeTokenWithType(userId, updateEmailToken, OneTimeTokenType.UPDATE_EMAIL);
        return oneTimeTokenService.extractEmailFromToken(updateEmailToken);
    }

    private void checkAndRevokeAllOneTimeTokenWithType(Long userId, String oneTimeToken, OneTimeTokenType type) {
        oneTimeTokenService.checkOneTimeToken(oneTimeToken, type);
        oneTimeTokenService.revokeAllOneTimeTokenWithType(userId, type);
    }
}
