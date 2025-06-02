package org.y9nba.app.dto.user;

import lombok.Getter;
import lombok.Setter;
import org.y9nba.app.dto.admin.AdminCreateDto;
import org.y9nba.app.util.PasswordUtil;

@Getter
@Setter
public class UserCreateDto {
    private String username;
    private String email;
    private String hashPassword;
    private boolean oauth2;

    public UserCreateDto(String username, String email, String hashPassword) {
        this(username, email, hashPassword, false);
    }

    public UserCreateDto(String username, String email, String hashPassword, boolean oauth2) {
        this.username = username;
        this.email = email;
        this.hashPassword = hashPassword;
        this.oauth2 = oauth2;
    }
}
