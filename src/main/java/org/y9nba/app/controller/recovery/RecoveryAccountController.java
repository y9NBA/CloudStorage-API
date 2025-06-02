package org.y9nba.app.controller.recovery;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.y9nba.app.dto.response.Response;
import org.y9nba.app.dto.user.update.UserEmailRequestDto;
import org.y9nba.app.service.impl.user.UserActivationServiceImpl;
import org.y9nba.app.service.impl.user.UserPasswordServiceImpl;

@Tag(
        name = "Recovery Account Controller",
        description = "Восстановление аккаунта"
)
@RestController
@RequestMapping("/recovery")
public class RecoveryAccountController {

    private final UserActivationServiceImpl userActivationService;
    private final UserPasswordServiceImpl userPasswordService;

    public RecoveryAccountController(UserActivationServiceImpl userActivationService, UserPasswordServiceImpl userPasswordService) {
        this.userActivationService = userActivationService;
        this.userPasswordService = userPasswordService;
    }

    @PostMapping("/resend-activation")
    @Operation(summary = "Повторная отправка активации")
    @ApiResponse(
            responseCode = "200",
            description = "Письмо с активацией отправлено на почту",
            content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = Response.class)
            )
    )
    public Response activateUser (@RequestBody UserEmailRequestDto dto) {
        return new Response(userActivationService.resendActivationByEmail(dto.getEmail()));
    }

    @PostMapping("/reset-password")
    @Operation(summary = "Сброс пароля")
    @ApiResponse(
            responseCode = "200",
            description = "Письмо с инструкциями для сброса пароля отправлено на почту",
            content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = Response.class)
            )
    )
    public Response resetPassword(@RequestBody UserEmailRequestDto dto) {
        return new Response(userPasswordService.resetPasswordByEmail(dto.getEmail()));
    }
}
