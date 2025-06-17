package org.y9nba.app.dto.user.update;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class UserUpdateEmailDto {
    @NotBlank(message = "Электронная почта не может быть пустой")
    private String email;
}
