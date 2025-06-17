package org.y9nba.app.dto.user.update;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class UserEmailRequestDto {
    @NotBlank(message = "Электронная почта не может быть пустой")
    private final String email;
}
