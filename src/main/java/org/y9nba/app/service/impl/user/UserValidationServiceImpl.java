package org.y9nba.app.service.impl.user;

import org.springframework.stereotype.Service;
import org.y9nba.app.dao.repository.UserRepository;
import org.y9nba.app.exception.web.user.info.email.EmailAlreadyException;
import org.y9nba.app.exception.web.user.info.email.EmailDuplicateException;
import org.y9nba.app.exception.web.user.info.email.NotValidEmailException;
import org.y9nba.app.exception.web.user.info.password.NotValidPasswordException;
import org.y9nba.app.exception.web.user.info.password.PasswordDuplicateException;
import org.y9nba.app.exception.web.user.info.password.PasswordIncorrectException;
import org.y9nba.app.exception.web.user.info.username.NotValidUsernameException;
import org.y9nba.app.exception.web.user.info.username.UsernameAlreadyException;
import org.y9nba.app.exception.web.user.info.username.UsernameDuplicateException;
import org.y9nba.app.service.face.user.UserValidationService;
import org.y9nba.app.util.PasswordUtil;
import org.y9nba.app.util.StringUtil;

@Service
public class UserValidationServiceImpl implements UserValidationService {

    private final UserRepository repository;
    private final StringUtil stringUtil;
    private final PasswordUtil passwordUtil;

    public UserValidationServiceImpl(UserRepository repository, StringUtil stringUtil, PasswordUtil passwordUtil) {
        this.repository = repository;
        this.stringUtil = stringUtil;
        this.passwordUtil = passwordUtil;
    }

    @Override
    public void checkCreateUser(String username, String email, String password) {
        checkUsername(username);
        checkEmail(email);
        checkPassword(password);
    }

    @Override
    public void checkUsername(String username) {
        if (repository.existsByUsername(username)) {
            throw new UsernameAlreadyException();
        } else if (!stringUtil.isValidUsername(username)) {
            throw new NotValidUsernameException();
        }
    }

    @Override
    public void checkEmail(String email) {
        if (repository.existsByEmail(email)) {
            throw new EmailAlreadyException();
        } else if (!stringUtil.isValidEmail(email)) {
            throw new NotValidEmailException();
        }
    }

    @Override
    public void checkPassword(String password) {
        if (!stringUtil.isValidPassword(password)) {
            throw new NotValidPasswordException();
        }
    }

    @Override
    public void checkResetPassword(String newPassword, String currentPassword) {
        if (passwordUtil.matches(newPassword, currentPassword)) {
            throw new PasswordDuplicateException();
        } else {
            checkPassword(newPassword);
        }
    }

    @Override
    public void checkUpdatePassword(String oldPassword, String newPassword, String currentPassword) {
        if (!passwordUtil.matches(oldPassword, currentPassword)) {
            throw new PasswordIncorrectException();
        } else {
            checkResetPassword(newPassword, currentPassword);
        }
    }

    @Override
    public void checkUpdateUsername(String oldUsername, String newUsername) {
        if (newUsername.equals(oldUsername)) {
            throw new UsernameDuplicateException();
        } else {
            checkUsername(newUsername);
        }
    }

    @Override
    public void checkUpdateEmail(String oldEmail, String newEmail) {
        if (newEmail.equals(oldEmail)) {
            throw new EmailDuplicateException();
        } else {
            checkEmail(newEmail);
        }
    }
}
