package org.y9nba.app.dto.auth;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class RegistrationRequestDto {

    @NotBlank(message = "Имя пользователя не может быть пустым")
    @Schema(description = "Уникальное имя пользователя", example = "john_doe")
    private final String username;

    @NotBlank(message = "Пароль не может быть пустым")
    @Schema(description = "Пароль (мин. 8 символов)", example = "securePassword123")
    private final String password;

    @NotBlank(message = "Email не может быть пустым")
    @Schema(description = "Валидный email адрес", example = "john@example.com")
    private final String email;
}
