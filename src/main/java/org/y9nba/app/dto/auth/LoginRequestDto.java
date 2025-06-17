package org.y9nba.app.dto.auth;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class LoginRequestDto {

    @NotBlank(message = "Логин не может быть пустым")
    @Schema(description = "Имя или почта", example = "john_doe|john@example.com")
    private final String login;

    @NotBlank(message = "Пароль не может быть пустым")
    @Schema(description = "Пароль", example = "securePassword123")
    private final String password;
}
