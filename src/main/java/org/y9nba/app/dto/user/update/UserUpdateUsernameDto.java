package org.y9nba.app.dto.user.update;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class UserUpdateUsernameDto {
    @NotBlank(message = "Имя пользователя не может быть пустым")
    private String username;
}
