package org.y9nba.app.dto.user;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class UserCreateDto {
    private String username;
    private String email;
    private String hashPassword;
}
