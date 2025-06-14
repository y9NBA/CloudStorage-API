package org.y9nba.app.util;

import org.springframework.stereotype.Component;

import java.util.regex.Pattern;

@Component
public class StringUtil {

    private final static Pattern EMAIL_REGEX = Pattern.compile("^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$");
    private final static Pattern PASSWORD_REGEX = Pattern.compile("^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[-_/@$!%*?&])[A-Za-z\\d-_/@$!%*?&]{8,}$");
    private final static Pattern USERNAME_REGEX = Pattern.compile("^[\\p{L}0-9_.%-]{3,30}$");

    public boolean isValidEmail(String email) {
        return EMAIL_REGEX.matcher(email).matches();
    }

    public boolean isValidPassword(String password) {
        return PASSWORD_REGEX.matcher(password).matches();
    }

    public boolean isValidUsername(String username) {
        return USERNAME_REGEX.matcher(username).matches();
    }
}
