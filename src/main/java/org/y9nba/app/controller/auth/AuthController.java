package org.y9nba.app.controller.auth;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.y9nba.app.dto.auth.AuthenticationResponseDto;
import org.y9nba.app.dto.auth.LoginRequestDto;
import org.y9nba.app.dto.auth.RegistrationRequestDto;
import org.y9nba.app.dto.response.Response;
import org.y9nba.app.exception.EmailAlreadyException;
import org.y9nba.app.exception.UsernameAlreadyException;
import org.y9nba.app.security.AuthenticationService;
import org.y9nba.app.service.UserService;

@Tag(
        name = "Контроллер авторизации",
        description = "Позволяет зарегистрироваться и войти в свою учётную запись"
)
@RestController
@RequestMapping("/auth")
public class AuthController {

    private final AuthenticationService authenticationService;
    private final UserService userService;

    public AuthController(AuthenticationService authenticationService, UserService userService) {
        this.authenticationService = authenticationService;
        this.userService = userService;
    }

    @Operation(
            summary = "Запрос на регистрацию",
            description = "Регистрация нового пользователя в системе"
    )
    @PostMapping("/registration")
    public Response register(@RequestBody RegistrationRequestDto registrationDto) {

        if(userService.existsByUsername(registrationDto.getUsername())) {
            throw new UsernameAlreadyException();
        }

        if(userService.existsByEmail(registrationDto.getEmail())) {
            throw new EmailAlreadyException();
        }

        authenticationService.register(registrationDto);

        return new Response("Регистрация прошла успешно");
    }

    @Operation(
            summary = "Запрос на вход",
            description = "Вход в уже существующую учётную запись"
    )
    @PostMapping("/login")
    public AuthenticationResponseDto authenticate(@RequestBody LoginRequestDto request) {
        return authenticationService.authenticate(request);
    }

    @Operation(
            summary = "Обновление токена",
            description = "Сносит все токены и отправляет свеженький"
    )
    @PostMapping("/refresh_token")
    public AuthenticationResponseDto refreshToken(HttpServletRequest request, HttpServletResponse response) {
        return authenticationService.refreshToken(request, response);
    }
}
