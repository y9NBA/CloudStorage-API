package org.y9nba.app.controller.confirm;

import org.springframework.web.bind.annotation.*;
import org.y9nba.app.dto.response.Response;
import org.y9nba.app.dto.user.UserResetPasswordDto;
import org.y9nba.app.service.impl.OneTimeTokenServiceImpl;
import org.y9nba.app.service.impl.UserServiceImpl;

import java.util.UUID;

@RestController
@RequestMapping("/confirm")
public class ConfirmController {
    private final UserServiceImpl userService;
    private final OneTimeTokenServiceImpl oneTimeTokenService;

    public ConfirmController(UserServiceImpl userService, OneTimeTokenServiceImpl oneTimeTokenService) {
        this.userService = userService;
        this.oneTimeTokenService = oneTimeTokenService;
    }

    @GetMapping("/activate/{tokenId}")
    public Response activate(@PathVariable UUID tokenId) {
        String activationToken = oneTimeTokenService.findTokenById(tokenId);
        Long userId = oneTimeTokenService.extractUserIdFromToken(activationToken);
        return new Response(userService.activateUser(userId, activationToken));
    }

    @GetMapping("/update-email/{tokenId}")
    public Response updateEmail(@PathVariable UUID tokenId) {
        String updateEmailToken = oneTimeTokenService.findTokenById(tokenId);
        Long userId = oneTimeTokenService.extractUserIdFromToken(updateEmailToken);
        return new Response(userService.updateEmail(userId, updateEmailToken));
    }

    @GetMapping("/reset-password/{tokenId}")
    public Response resetPassword(@PathVariable UUID tokenId) {
        String resetPasswordToken = oneTimeTokenService.findTokenById(tokenId);
        Long userId = oneTimeTokenService.extractUserIdFromToken(resetPasswordToken);
        return new Response(userService.resetPassword(userId, null, resetPasswordToken));
    }

    @PostMapping("/reset-password/{tokenId}")
    public Response resetPassword(@PathVariable UUID tokenId, @RequestBody UserResetPasswordDto dto) {
        String resetPasswordToken = oneTimeTokenService.findTokenById(tokenId);
        Long userId = oneTimeTokenService.extractUserIdFromToken(resetPasswordToken);
        return new Response(userService.resetPassword(userId, dto, resetPasswordToken));
    }

    @GetMapping("/rollback-password/{tokenId}")
    public Response rollbackPassword(@PathVariable UUID tokenId) {
        String rollbackPasswordToken = oneTimeTokenService.findTokenById(tokenId);
        Long userId = oneTimeTokenService.extractUserIdFromToken(rollbackPasswordToken);
        return new Response(userService.rollbackPassword(userId, rollbackPasswordToken));
    }

    @GetMapping("/rollback-email/{tokenId}")
    public Response rollbackEmail(@PathVariable UUID tokenId) {
        String rollbackEmailToken = oneTimeTokenService.findTokenById(tokenId);
        Long userId = oneTimeTokenService.extractUserIdFromToken(rollbackEmailToken);
        return new Response(userService.rollbackEmail(userId, rollbackEmailToken));
    }
}
