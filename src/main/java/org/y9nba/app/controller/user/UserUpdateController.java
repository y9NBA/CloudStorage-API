package org.y9nba.app.controller.user;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.y9nba.app.dto.response.Response;
import org.y9nba.app.dto.user.update.UserUpdateDto;
import org.y9nba.app.dto.user.update.UserUpdateEmailDto;
import org.y9nba.app.dto.user.update.UserUpdatePasswordDto;
import org.y9nba.app.dto.user.update.UserUpdateUsernameDto;
import org.y9nba.app.dao.entity.User;
import org.y9nba.app.service.impl.user.UserDeleteServiceImpl;
import org.y9nba.app.service.impl.user.UserEmailServiceImpl;
import org.y9nba.app.service.impl.user.UserServiceImpl;

@Tag(
        name = "User  Update",
        description = "Контроллер для обновления данных аутентифицированного пользователя"
)
@RestController
@RequestMapping("/user/update")
@ApiResponses({
        @ApiResponse(
                responseCode = "401",
                description = "Пользователь не авторизован",
                content = @Content(
                        mediaType = "application/json",
                        schema = @Schema(implementation = Response.class),
                        examples = @ExampleObject(value = "{\"message\": \"UNAUTHORIZED\"}")
                )
        )
})
public class UserUpdateController {

    private final UserServiceImpl userService;
    private final UserEmailServiceImpl userEmailService;
    private final UserDeleteServiceImpl userDeleteService;

    public UserUpdateController(UserServiceImpl userService, UserEmailServiceImpl userEmailService, UserDeleteServiceImpl userDeleteService) {
        this.userService = userService;
        this.userEmailService = userEmailService;
        this.userDeleteService = userDeleteService;
    }

    @PreAuthorize("hasAuthority('UPDATE_PASSWORD')")
    @Operation(
            summary = "Обновить пароль",
            description = "Обновляет пароль пользователя"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Пароль успешно обновлен",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = Response.class),
                            examples = @ExampleObject(value = "{\"message\": \"Пароль успешно обновлен\"}")
                    )
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Некорректные данные для обновления пароля",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = Response.class),
                            examples = @ExampleObject(value = "{\"message\": \"Некорректные данные\"}")
                    )),
            @ApiResponse(
                    responseCode = "409",
                    description = "Неверный текущий пароль или новый пароль совпадает с текущим",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = Response.class),
                            examples = @ExampleObject(value = "{\"message\": \"Неверный текущий пароль\"}")
                    ))
    })

    @PutMapping("/password")
    public Response updatePassword(@AuthenticationPrincipal User user, @RequestBody UserUpdatePasswordDto dto) {
        userService.update(user.getId(), dto);
        return new Response("Пароль успешно обновлен");
    }

    @PreAuthorize("hasAuthority('UPDATE_EMAIL')")
    @Operation(
            summary = "Обновить email",
            description = "Отправляет письмо с подтверждением на новую почту")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "На новую почту отправлено письмо с подтверждением",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = Response.class),
                            examples = @ExampleObject(value = "{\"message\": \"На новую почту отправлено письмо с подтверждением\"}")
                    )),
            @ApiResponse(
                    responseCode = "400",
                    description = "Некорректные данные для обновления email",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = Response.class),
                            examples = @ExampleObject(value = "{\"message\": \"Некорректные данные\"}")
                    )),
            @ApiResponse(
                    responseCode = "409",
                    description = "Email уже занят или совпадает с текущим",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = Response.class),
                            examples = @ExampleObject(value = "{\"message\": \"Email уже занят\"}")
                    ))
    })
    @PutMapping("/email")
    public Response updateEmail(@AuthenticationPrincipal User user, @RequestBody UserUpdateEmailDto dto) {
        String res = userEmailService.tryUpdateEmail(user.getId(), dto);
        return new Response(res);
    }

    @PreAuthorize("hasAuthority('UPDATE_USERNAME')")
    @Operation(
            summary = "Обновить имя",
            description = "Обновляет имя пользователя")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Username успешно обновлен на: что-то",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = Response.class),
                            examples = @ExampleObject(value = "{\"message\": \"Username успешно обновлен\"}")
                    )),
            @ApiResponse(
                    responseCode = "400",
                    description = "Некорректные данные для обновления имени пользователя",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = Response.class),
                            examples = @ExampleObject(value = "{\"message\": \"Некорректные данные\"}")
                    )),
            @ApiResponse(
                    responseCode = "409",
                    description = "Имя пользователя уже занято или совпадает с текущим",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = Response.class),
                            examples = @ExampleObject(value = "{\"message\": \"Имя пользователя уже занято\"}")
                    ))
    })
    @PutMapping("/username")
    public Response updateUsername(@AuthenticationPrincipal User user, @RequestBody UserUpdateUsernameDto dto) {
        userService.update(user.getId(), dto);
        return new Response("Username успешно обновлен на: " + dto.getUsername());
    }

    @PreAuthorize("hasAuthority('UPDATE_PROFILE')")
    @Operation(summary = "Обновить все данные пользователя", description = "Обновляет все данные пользователя.")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Данные успешно обновлены",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = Response.class),
                            examples = @ExampleObject(value = "{\"message\": \"Данные успешно обновлены\"}")
                    )),
            @ApiResponse(
                    responseCode = "400",
                    description = "Некорректные данные для обновления",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = Response.class),
                            examples = @ExampleObject(value = "{\"message\": \"Некорректные данные\"}")
                    )),
            @ApiResponse(
                    responseCode = "409",
                    description = "Конфликт данных при обновлении (например, дублирующий email или имя)",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = Response.class),
                            examples = @ExampleObject(value = "{\"message\": \"Конфликт данных\"}")
                    ))
    })
    @PutMapping("/all")
    public Response updateUser(@AuthenticationPrincipal User user, @RequestBody UserUpdateDto dto) {
        userService.update(
                user.getId(),
                new UserUpdatePasswordDto(
                        dto.getOldPassword(),
                        user.getPassword()
                )
        );

        userService.update(
                user.getId(),
                new UserUpdateUsernameDto(
                        dto.getUsername()
                )
        );

        String resUpdEmail = userEmailService.tryUpdateEmail(
                user.getId(),
                new UserUpdateEmailDto(dto.getEmail())
        );

        return new Response("Данные успешно обновлены. " + resUpdEmail);
    }

    @PreAuthorize("hasAuthority('DELETE_PROFILE')")
    @Operation(
            summary = "Удалить аккаунт",
            description = "Отправляет письмо для подтверждения удаления аккаунта"
    )
    @ApiResponse(
            responseCode = "200",
            description = "Данные успешно обновлены",
            content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = Response.class),
                    examples = @ExampleObject(value = "{\"message\": \"На почту отправлено письмо с подтверждением удаления аккаунта\"}")
            ))
    @DeleteMapping("/delete-account")
    public Response deleteAccount(@AuthenticationPrincipal User user) {
        return new Response(userDeleteService.deleteUserByEmail(user.getEmail()));
    }
}
