package org.y9nba.app.controller.auth;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.y9nba.app.dto.auth.TokenResponseDto;
import org.y9nba.app.dto.auth.LoginRequestDto;
import org.y9nba.app.dto.auth.RegistrationRequestDto;
import org.y9nba.app.dto.response.ErrorResponse;
import org.y9nba.app.dto.response.Response;
import org.y9nba.app.exception.EmailAlreadyException;
import org.y9nba.app.exception.UsernameAlreadyException;
import org.y9nba.app.security.AuthenticationService;
import org.y9nba.app.service.UserService;

@Tag(
        name = "Authentication Controller",
        description = "Регистрация, аутентификация и управление токенами доступа"
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
            description = "Регистрация нового пользователя в системе. Логин и email должны быть уникальными",
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Данные для регистрации",
                    required = true,
                    content = @Content(
                            schema = @Schema(implementation = RegistrationRequestDto.class),
                            examples = @ExampleObject(
                                    value = """
                                                {
                                                  "username": "john_doe",
                                                  "email": "john@example.com",
                                                  "password": "securePassword123"
                                                }
                                            """
                            )
                    )
            )
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Успешная регистрация",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = Response.class),
                            examples = @ExampleObject(
                                    value = "{\"message\": \"Регистрация прошла успешно\"}"
                            )
                    )),
            @ApiResponse(
                    responseCode = "409",
                    description = "Конфликт данных",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = Response.class),
                            examples = {
                                    @ExampleObject(name = "Username conflict", value = "{\"message\": \"Имя пользователя уже занято\"}"),
                                    @ExampleObject(name = "Email conflict", value = "{\"message\": \"Такой email уже занят\"}")
                            }
                    ))
    })
    @PostMapping("/registration")
    public Response register(@RequestBody RegistrationRequestDto registrationDto) {

        if (userService.existsByUsername(registrationDto.getUsername())) {
            throw new UsernameAlreadyException();
        }

        if (userService.existsByEmail(registrationDto.getEmail())) {
            throw new EmailAlreadyException();
        }

        authenticationService.register(registrationDto);

        return new Response("Регистрация прошла успешно");
    }

    @Operation(
            summary = "Запрос на вход",
            description = "Аутентификация пользователя и получение JWT токенов",
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Учетные данные пользователя",
                    required = true,
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = LoginRequestDto.class),
                            examples = @ExampleObject(
                                    value = """
                                                {
                                                  "username": "john_doe",
                                                  "password": "securePassword123"
                                                }
                                            """
                            )
                    )
            )
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Успешная аутентификация",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = TokenResponseDto.class))),
            @ApiResponse(
                    responseCode = "401",
                    description = "Ошибка в данных",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = Response.class),
                            examples = @ExampleObject(value = "{\"message\": \"Неверные учетные данные пользователя\"}"))
            )
    })
    @PostMapping("/login")
    public TokenResponseDto authenticate(@RequestBody LoginRequestDto request) {
        return authenticationService.authenticate(request);
    }

    @Operation(
            summary = "Обновление токена",
            description = "Генерация новой пары access/refresh токенов по валидному refresh токену"
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Токены успешно обновлены",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = TokenResponseDto.class))
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "Невалидный или отсутствующий refresh токен",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class),
                            examples = @ExampleObject(value = "{\"message\": \"UNAUTHORIZED\"}")
                    )
            )
    })
    @PostMapping("/refresh_token")
    public TokenResponseDto refreshToken(HttpServletRequest request, HttpServletResponse response) {
        return authenticationService.refreshToken(request, response);
    }
}
