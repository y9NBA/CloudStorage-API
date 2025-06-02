package org.y9nba.app.service.face.email;

import org.y9nba.app.dao.entity.User;

public interface ConfirmService {
    String sendUpdateEmailConfirmation(User user, String newEmail);
    void sendRollbackUpdateEmailConfirmation(User user, String oldEmail);
    String sendActivateAccountConfirmation(User user);
    String sendResetPasswordConfirmation(User user);
    String sendResetPasswordInformation(User user, String newPassword);
    String sendResetPasswordInformation(User user);
    String sendAccountDeleteConfirmation(User user);
    void confirmActivateAccount(Long userId, String activationToken);
    void confirmResetPassword(Long userId, String resetPasswordToken);
    String confirmRollbackPassword(Long userId, String rollbackPasswordToken);
    String confirmRollbackEmail(Long userId, String rollbackEmailToken);
    void confirmDeleteAccount(Long userId, String deleteAccountToken);
    String confirmEmailChange(Long userId, String updateEmailToken);
}
