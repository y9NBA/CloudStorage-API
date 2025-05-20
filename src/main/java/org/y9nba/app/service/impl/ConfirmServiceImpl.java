package org.y9nba.app.service.impl;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.y9nba.app.constant.OneTimeTokenType;
import org.y9nba.app.model.UserModel;
import org.y9nba.app.service.ConfirmService;

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
    public String sendUpdateEmailConfirmation(UserModel userModel, String newEmail) {
        UUID tokenId = oneTimeTokenService.createUpdateEmailToken(userModel, newEmail);

        emailService.sendUpdateEmailConfirmationMessage(
                newEmail,
                domainURL + "confirm/update-email/" + tokenId
        );

        return "Письмо с подтверждением смены почты отправлено на: " + newEmail;
    }

    @Override
    public void sendRollbackUpdateEmailConfirmation(UserModel userModel, String oldEmail) {
        UUID tokenId = oneTimeTokenService.createRollbackEmailToken(userModel, oldEmail);

        emailService.sendRollbackUpdateEmailConfirmationMessage(
                oldEmail,
                domainURL + "confirm/rollback-email/" + tokenId
        );
    }

    @Override
    public String sendActivateAccountConfirmation(UserModel userModel) {
        UUID tokenId = oneTimeTokenService.createActivationToken(userModel);

        emailService.sendActivationAccountConfirmationMessage(
                userModel.getEmail(),
                domainURL + "confirm/activate/" + tokenId
        );

        return "Письмо с подтверждением регистрации отправлено на: " + userModel.getEmail() + " :)";
    }

    @Override
    public String sendResetPasswordConfirmation(UserModel userModel) {
        UUID tokenId = oneTimeTokenService.createResetPasswordToken(userModel);

        emailService.sendResetPasswordConfirmationMessage(
                userModel.getEmail(),
                domainURL + "confirm/reset-password/" + tokenId
        );

        return "Письмо с инструкцией по сбросу пароля отправлено на: " + userModel.getEmail();
    }

    @Override
    public String sendAccountDeletionConfirmation(UserModel userModel) {
        return null;
    }

    @Override
    public String sendResetPasswordInformation(UserModel userModel, String newPassword) {
        UUID tokenId = oneTimeTokenService.createRollbackPasswordToken(userModel);

        emailService.sendResetPasswordInfoMessage(
                userModel.getEmail(),
                userModel.getUsername(),
                newPassword,
                domainURL + "confirm/rollback-password/" + tokenId

        );

        return "Информационное письмо с новым паролем отправлено на: " + userModel.getEmail();
    }

    @Override
    public String sendResetPasswordInformation(UserModel userModel) {
        UUID tokenId = oneTimeTokenService.createRollbackPasswordToken(userModel);

        emailService.sendResetPasswordInfoMessage(
                userModel.getEmail(),
                domainURL + "confirm/rollback-password/" + tokenId

        );

        return "Пароль успешно изменён";
    }

    @Override
    public String sendAuthenticationInformation(UserModel userModel) {
        return null;
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
