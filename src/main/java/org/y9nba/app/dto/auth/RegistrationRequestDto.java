package org.y9nba.app.dto.auth;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

@AllArgsConstructor
@Getter
public class RegistrationRequestDto {
    private String username;
    private String email;
    private String password;
}
