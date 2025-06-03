package org.y9nba.app.controller.confirm;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.*;
import org.y9nba.app.dto.response.Response;
import org.y9nba.app.dto.user.update.UserResetPasswordDto;
import org.y9nba.app.service.impl.token.onetime.OneTimeTokenServiceImpl;
import org.y9nba.app.service.impl.user.UserActivationServiceImpl;
import org.y9nba.app.service.impl.user.UserDeleteServiceImpl;
import org.y9nba.app.service.impl.user.UserEmailServiceImpl;
import org.y9nba.app.service.impl.user.UserPasswordServiceImpl;

@Tag(
        name = "Confirm Controller",
        description = "Обработка запросов с использованием одноразовых токенов подтверждения"
)
@RestController
@RequestMapping("/confirm")
@ApiResponses({
        @ApiResponse(
                responseCode = "410",
                description = "Ссылка недействительна",
                content = @Content(
                        mediaType = "application/json",
                        schema = @Schema(implementation = Response.class),
                        examples = @ExampleObject(value = "{\"message\": \"Данная ссылка устарела или уже была использована.\"}")
                )
        )
})
public class ConfirmController {

    private final OneTimeTokenServiceImpl oneTimeTokenService;
    private final UserActivationServiceImpl userActivationService;
    private final UserEmailServiceImpl userEmailService;
    private final UserPasswordServiceImpl userPasswordService;
    private final UserDeleteServiceImpl userDeleteService;

    public ConfirmController(OneTimeTokenServiceImpl oneTimeTokenService, UserActivationServiceImpl userActivationService, UserEmailServiceImpl userEmailService, UserPasswordServiceImpl userPasswordService, UserDeleteServiceImpl userDeleteService) {
        this.oneTimeTokenService = oneTimeTokenService;
        this.userActivationService = userActivationService;
        this.userEmailService = userEmailService;
        this.userPasswordService = userPasswordService;
        this.userDeleteService = userDeleteService;
    }

    @GetMapping("/activate/token")
    @Operation(
            summary = "Активация пользователя по токену",
            description = "Активирует пользователя, используя токен активации."
    )
    @ApiResponse(
            responseCode = "200",
            description = "Пользователь успешно активирован",
            content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = Response.class)
            )
    )
    public Response activate(@RequestParam(name = "key") String activationToken) {
        Long userId = oneTimeTokenService.getUserIdByOneTimeToken(activationToken);
        return new Response(userActivationService.activateUser (userId, activationToken));
    }

    @GetMapping("/update-email/token")
    @Operation(
            summary = "Обновление email по токену",
            description = "Обновляет email пользователя, используя токен обновления email."
    )
    @ApiResponse(
            responseCode = "200",
            description = "Email успешно обновлен",
            content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = Response.class)
            )
    )
    public Response updateEmail(@RequestParam(name = "key") String updateEmailToken) {
        Long userId = oneTimeTokenService.getUserIdByOneTimeToken(updateEmailToken);
        return new Response(userEmailService.updateEmail(userId, updateEmailToken));
    }

    @GetMapping("/reset-password/token")
    @Operation(
            summary = "Сброс пароля по токену",
            description = "Сбрасывает пароль пользователя, используя токен сброса пароля."
    )
    @ApiResponse(
            responseCode = "200",
            description = "Информационное письмо с новым паролем отправлено на почту",
            content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = Response.class)
            )
    )
    public Response resetPassword(@RequestParam(name = "key") String resetPasswordToken) {
        Long userId = oneTimeTokenService.getUserIdByOneTimeToken(resetPasswordToken);
        return new Response(userPasswordService.resetPassword(userId, null, resetPasswordToken));
    }

    @PostMapping("/reset-password/token")
    @Operation(
            summary = "Сброс пароля с новым паролем",
            description = "Сбрасывает пароль пользователя, используя токен сброса пароля и новый пароль."
    )
    @ApiResponse(
            responseCode = "200",
            description = "Пароль успешно изменен",
            content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = Response.class)
            )
    )
    public Response resetPassword(@RequestParam(name = "key") String resetPasswordToken, @RequestBody UserResetPasswordDto dto) {
        Long userId = oneTimeTokenService.getUserIdByOneTimeToken(resetPasswordToken);
        return new Response(userPasswordService.resetPassword(userId, dto, resetPasswordToken));
    }

    @GetMapping("/rollback-password/token")
    @Operation(
            summary = "Откат пароля по токену",
            description = "Откатывает сброс пароля, используя токен отката пароля."
    )
    @ApiResponse(
            responseCode = "200",
            description = "Сброс пароля успешно отменен",
            content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = Response.class)
            )
    )
    public Response rollbackPassword(@RequestParam(name = "key") String rollbackPasswordToken) {
        Long userId = oneTimeTokenService.getUserIdByOneTimeToken(rollbackPasswordToken);
        return new Response(userPasswordService.rollbackPassword(userId, rollbackPasswordToken));
    }

    @GetMapping("/rollback-email/token")
    @Operation(
            summary = "Откат email по токену",
            description = "Откатывает изменение email, используя токен отката email."
    )
    @ApiResponse(
            responseCode = "200",
            description = "Email остался прежним",
            content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = Response.class)
            )
    )
    public Response rollbackEmail(@RequestParam(name = "key") String rollbackEmailToken) {
        Long userId = oneTimeTokenService.getUserIdByOneTimeToken(rollbackEmailToken);
        return new Response(userEmailService.rollbackEmail(userId, rollbackEmailToken));
    }

    @GetMapping("/delete-account/token")
    @Operation(
            summary = "Удаление аккаунта по токену",
            description = "Удаляет аккаунт пользователя, используя токен удаления аккаунта."
    )
    @ApiResponse(
            responseCode = "200",
            description = "Аккаунт и все его данные удалены",
            content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = Response.class)
            )
    )
    public Response deleteAccount(@RequestParam(name = "key") String deleteAccountToken) {
        Long userId = oneTimeTokenService.getUserIdByOneTimeToken(deleteAccountToken);
        return new Response(userDeleteService.deleteUser (userId, deleteAccountToken));
    }
}
