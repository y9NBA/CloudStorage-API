package org.y9nba.app.util;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class PasswordUtil {
    private final PasswordEncoder passwordEncoder;

    private final static String ALLOWED_CHARS_UPPER = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
    private final static String ALLOWED_CHARS_LOWER = "abcdefghijklmnopqrstuvwxyz";
    private final static String ALLOWED_CHARS_NUMBER = "0123456789";
    private final static String ALLOWED_CHARS_SPECIAL = "-_[]./@$!%*?&";

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

    public static String getSpecialCharacters() {
        return ALLOWED_CHARS_SPECIAL;
    }

    private String generateRandomPasswordWithRequirements(Long length, boolean upperCase, boolean number, boolean special) {
        String allowedChars = ALLOWED_CHARS_LOWER;
        StringBuilder password = new StringBuilder();
        int charsInOn = 0;

        password.append(getRandomChar(ALLOWED_CHARS_LOWER));

        if (upperCase) {
            password.append(getRandomChar(ALLOWED_CHARS_UPPER));
            allowedChars += ALLOWED_CHARS_UPPER;
            charsInOn++;
        }

        if (number) {
            password.append(getRandomChar(ALLOWED_CHARS_NUMBER));
            allowedChars += ALLOWED_CHARS_NUMBER;
            charsInOn++;
        }

        if (special) {
            password.append(getRandomChar(ALLOWED_CHARS_SPECIAL));
            allowedChars += ALLOWED_CHARS_SPECIAL;
            charsInOn++;
        }


        for (int i = 0; i < length - charsInOn; i++) {
            password.append(getRandomChar(allowedChars));
        }

        return password.toString();
    }

    private char getRandomChar(String chars) {
        return chars.charAt((int) (Math.random() * chars.length()));
    }
}
