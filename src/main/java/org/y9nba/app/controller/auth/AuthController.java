package org.y9nba.app.controller.auth;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.y9nba.app.dto.auth.OAuth2ResponseDTO;
import org.y9nba.app.dto.auth.TokenResponseDto;
import org.y9nba.app.dto.auth.LoginRequestDto;
import org.y9nba.app.dto.auth.RegistrationRequestDto;
import org.y9nba.app.dto.response.ErrorResponse;
import org.y9nba.app.dto.response.Response;
import org.y9nba.app.security.AuthenticationService;

@Tag(
        name = "Authentication Controller",
        description = "Регистрация, аутентификация и управление токенами доступа"
)
@RestController
@RequestMapping("/auth")
public class AuthController {

    private final AuthenticationService authenticationService;

    private final static String baseOAuthUrl = "/oauth2/authorization/";
    private final static String baseSuccessUrl = "/login/oauth2/code/";

    public AuthController(AuthenticationService authenticationService) {
        this.authenticationService = authenticationService;
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
                                    value = "{\"message\": \"Письмо с подтверждением регистрации отправлено\"}"
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
    public Response register(@RequestBody @Validated RegistrationRequestDto registrationDto) {
        return new Response(authenticationService.register(registrationDto));
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
                                                  "login": "john_doe|john@example.com",
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
                    responseCode = "400",
                    description = "Ошибка в данных",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = Response.class),
                            examples = @ExampleObject(value = "{\"message\": \"Неверные учетные данные пользователя\"}"))
            )
    })
    @PostMapping("/login")
    public TokenResponseDto authenticate(@RequestBody @Validated LoginRequestDto loginRequestDto, HttpServletRequest request) {
        return authenticationService.authenticate(loginRequestDto, request);
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
    public TokenResponseDto refreshToken(HttpServletRequest request) {
        return authenticationService.refreshToken(request);
    }

    @Operation(
            summary = "Аутентификация через Google",
            description = "Аутентификация пользователя с использованием Google OAuth2"
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Успешная аутентификация через Google",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = TokenResponseDto.class))
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "Ошибка аутентификации через Google",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class),
                            examples = @ExampleObject(value = "{\"message\": \"Произошла какая-то ошибка при авторизации через Google\"}")
                    )
            )
    })
    @GetMapping("/login/oauth2/google")
    public OAuth2ResponseDTO authenticateWithGoogle() {
        return new OAuth2ResponseDTO(
                baseOAuthUrl + "google",
                baseSuccessUrl + "google"
        );
    }

    @Operation(
            summary = "Выход из системы",
            description = "Завершение сессии пользователя"
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "204",
                    description = "Успешный выход из системы"
            )
    })
    @GetMapping("/logout")
    public void logout() {    // Добавил для обозначения в Swagger
    }
}
