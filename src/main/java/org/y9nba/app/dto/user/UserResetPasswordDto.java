package org.y9nba.app.dto.user;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class UserResetPasswordDto {
    private String newPassword;
}
