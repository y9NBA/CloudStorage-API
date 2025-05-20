package org.y9nba.app.service;

import org.y9nba.app.model.UserModel;

public interface ConfirmService {
    String sendUpdateEmailConfirmation(UserModel userModel, String newEmail);

    void sendRollbackUpdateEmailConfirmation(UserModel userModel, String oldEmail);

    String sendActivateAccountConfirmation(UserModel userModel);
    String sendResetPasswordConfirmation(UserModel userModel);
    String sendResetPasswordInformation(UserModel userModel, String newPassword);
    String sendResetPasswordInformation(UserModel userModel);
    String sendAuthenticationInformation(UserModel userModel);
    String sendAccountDeletionConfirmation(UserModel userModel);
    void confirmActivateAccount(Long userId, String activationToken);
    void confirmResetPassword(Long userId, String resetPasswordToken);
    String confirmRollbackPassword(Long userId, String rollbackPasswordToken);
    String confirmRollbackEmail(Long userId, String rollbackEmailToken);
    void confirmDeleteAccount(Long userId, String deleteAccountToken);
    String confirmEmailChange(Long userId, String updateEmailToken);
}
