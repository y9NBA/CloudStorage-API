package org.y9nba.app.util;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class PasswordUtil {
    private final PasswordEncoder passwordEncoder;

    private final static String ALLOWED_CHARS_UPPER = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
    private final static String ALLOWED_CHARS_LOWER = "abcdefghijklmnopqrstuvwxyz";
    private final static String ALLOWED_CHARS_NUMBER = "0123456789";
    private final static String ALLOWED_CHARS_SPECIAL = "!@#$%^&*()_+-=[]{}|;':\",./<>?";

    public PasswordUtil(PasswordEncoder passwordEncoder) {
        this.passwordEncoder = passwordEncoder;
    }

    public String encode(String password) {
        return passwordEncoder.encode(password);
    }

    public boolean matches(String rawPassword, String encodedPassword) {
        return passwordEncoder.matches(rawPassword, encodedPassword);
    }

    public String generateRandomPassword(Long length) {
        return generateRandomPasswordWithRequirements(length, true, true, false);
    }

    public String generatePasswordWithSpecial(Long length) {
        return generateRandomPasswordWithRequirements(length, true, true, true);
    }

    private String generateRandomPasswordWithRequirements(Long length, boolean upperCase, boolean number, boolean special) {
        String allowedChars = ALLOWED_CHARS_LOWER;

        if (upperCase)
            allowedChars += ALLOWED_CHARS_UPPER;

        if (number)
            allowedChars += ALLOWED_CHARS_NUMBER;

        if (special)
            allowedChars += ALLOWED_CHARS_SPECIAL;

        StringBuilder password = new StringBuilder();

        for (int i = 0; i < length; i++) {
            password.append(allowedChars.charAt((int) (Math.random() * allowedChars.length())));
        }

        return password.toString();
    }
}
