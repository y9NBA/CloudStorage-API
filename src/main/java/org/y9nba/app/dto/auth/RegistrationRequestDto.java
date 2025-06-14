package org.y9nba.app.dto.auth;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class RegistrationRequestDto {

    @Schema(description = "Уникальное имя пользователя", example = "john_doe")
    private final String username;

    @Schema(description = "Пароль (мин. 8 символов)", example = "securePassword123")
    private final String password;

    @Schema(description = "Валидный email адрес", example = "john@example.com")
    private final String email;
}
