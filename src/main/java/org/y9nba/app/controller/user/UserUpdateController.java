package org.y9nba.app.controller.user;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.y9nba.app.dto.response.Response;
import org.y9nba.app.dto.user.UserUpdateDto;
import org.y9nba.app.dto.user.UserUpdateEmailDto;
import org.y9nba.app.dto.user.UserUpdatePasswordDto;
import org.y9nba.app.dto.user.UserUpdateUsernameDto;
import org.y9nba.app.model.UserModel;
import org.y9nba.app.service.impl.UserServiceImpl;

@RestController
@RequestMapping("/user/update")
@Tag(name = "User  Update", description = "Контроллер для обновления данных аутентифицированного пользователя")
public class UserUpdateController {

    private final UserServiceImpl userService;

    public UserUpdateController(UserServiceImpl userService) {
        this.userService = userService;
    }

    @Operation(summary = "Обновить пароль пользователя", description = "Обновляет пароль пользователя.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Пароль успешно обновлен"),
            @ApiResponse(responseCode = "400", description = "Некорректные данные для обновления пароля"),
            @ApiResponse(responseCode = "401", description = "Пользователь не аутентифицирован"),
            @ApiResponse(responseCode = "409", description = "Неверный текущий пароль или новый пароль совпадает с текущим")
    })
    @PutMapping("/password")
    public Response updatePassword(@AuthenticationPrincipal UserModel user, @RequestBody UserUpdatePasswordDto dto) {
        userService.update(user.getId(), dto);
        return new Response("Пароль успешно обновлен");
    }

    @Operation(summary = "Обновить email пользователя", description = "Обновляет email пользователя.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Email успешно обновлен на: что-то"),
            @ApiResponse(responseCode = "400", description = "Некорректные данные для обновления email"),
            @ApiResponse(responseCode = "401", description = "Пользователь не аутентифицирован"),
            @ApiResponse(responseCode = "409", description = "Email уже занят или совпадает с текущим")
    })
    @PutMapping("/email")
    public Response updateEmail(@AuthenticationPrincipal UserModel user, @RequestBody UserUpdateEmailDto dto) {
        userService.update(user.getId(), dto);
        return new Response("Email успешно обновлен на: " + dto.getEmail());
    }

    @Operation(summary = "Обновить имя пользователя", description = "Обновляет имя пользователя.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Username успешно обновлен на: что-то"),
            @ApiResponse(responseCode = "400", description = "Некорректные данные для обновления имени пользователя"),
            @ApiResponse(responseCode = "401", description = "Пользователь не аутентифицирован"),
            @ApiResponse(responseCode = "409", description = "Имя пользователя уже занято или совпадает с текущим")
    })
    @PutMapping("/username")
    public Response updateUsername(@AuthenticationPrincipal UserModel user, @RequestBody UserUpdateUsernameDto dto) {
        userService.update(user.getId(), dto);
        return new Response("Username успешно обновлен на: " + dto.getUsername());
    }

    @Operation(summary = "Обновить все данные пользователя", description = "Обновляет все данные пользователя.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Данные успешно обновлены"),
            @ApiResponse(responseCode = "400", description = "Некорректные данные для обновления"),
            @ApiResponse(responseCode = "401", description = "Пользователь не аутентифицирован"),
            @ApiResponse(responseCode = "409", description = "Конфликт данных при обновлении (например, дублирующий email или имя)")
    })
    @PutMapping("/all")
    public Response updateUser(@AuthenticationPrincipal UserModel user, @RequestBody UserUpdateDto dto) {
        userService.update(user.getId(), dto);
        return new Response("Данные успешно обновлены");
    }
}
