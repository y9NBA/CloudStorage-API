package org.y9nba.app.dto.user.update;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class UserUpdateDto {
    @NotBlank(message = "Имя пользователя не может быть пустым")
    private String username;

    @NotBlank(message = "Старый пароль не может быть пустым")
    private String oldPassword;

    @NotBlank(message = "Новый пароль не может быть пустым")
    private String newPassword;

    @NotBlank(message = "Электронная почта не может быть пустой")
    private String email;
}
