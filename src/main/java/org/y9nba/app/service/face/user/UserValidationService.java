package org.y9nba.app.service.face.user;

public interface UserValidationService {
    void checkCreateUser(String username, String email, String password);
    void checkUsername(String username);
    void checkEmail(String email);
    void checkPassword(String password);
    void checkResetPassword(String oldPassword, String newPassword);
    void checkUpdatePassword(String oldPassword, String newPassword, String currentPassword);
    void checkUpdateUsername(String oldUsername, String newUsername);
    void checkUpdateEmail(String oldEmail, String newEmail);
}
