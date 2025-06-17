package org.y9nba.app.dto.user.update;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class UserResetPasswordDto {
    @NotBlank(message = "Поле с новым паролем не может быть пустым")
    private String newPassword;
}
