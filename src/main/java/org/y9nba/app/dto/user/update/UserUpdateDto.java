package org.y9nba.app.dto.user.update;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class UserUpdateDto {
    private String username;
    private String oldPassword;
    private String newPassword;
    private String email;
}
