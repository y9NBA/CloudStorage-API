package org.y9nba.app.service.face.email;

public interface EmailService {
    void sendActivationAccountConfirmationMessage(String email, String activationURL);
    void sendUpdateEmailConfirmationMessage(String email, String updateEmailURL);
    void sendResetPasswordConfirmationMessage(String email, String resetPasswordURL);
    void sendRollbackUpdateEmailConfirmationMessage(String email, String rollbackUpdateEmailURL);
    void sendAdminAccountInfoMessage(String email, String username, String password);
    void sendOAuth2AccountInfoMessage(String email, String username, String password);
    void sendResetPasswordInfoMessage(String email, String username, String newPassword, String rollbackPasswordURL);
    void sendResetPasswordInfoMessage(String email, String rollbackPasswordURL);
    void sendBannedInfoMessage(String email, String username);
    void sendUnbannedInfoMessage(String email, String username);
    void sendWarningOfPublicFileInfoMessage(String email, String fileURL);
    void sendNotificationOfPublicFileInfoMessage(String email, String fileURL);
    void sendDeleteAccountConfirmationMessage(String email, String deleteAccountURL);
}
