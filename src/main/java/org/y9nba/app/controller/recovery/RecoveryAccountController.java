package org.y9nba.app.controller.recovery;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.y9nba.app.dto.response.Response;
import org.y9nba.app.dto.user.UserEmailRequestDto;
import org.y9nba.app.service.impl.UserServiceImpl;

@RestController
@RequestMapping("/recovery")
public class RecoveryAccountController {

    private final UserServiceImpl userService;

    public RecoveryAccountController(UserServiceImpl userService) {
        this.userService = userService;
    }

    @PostMapping("/resend-activation")
    public Response activateUser(@RequestBody UserEmailRequestDto dto) {
        return new Response(userService.resendActivationByEmail(dto.getEmail()));
    }

    @PostMapping("/reset-password")
    public Response resetPassword(@RequestBody UserEmailRequestDto dto) {
        return new Response(userService.resetPasswordByEmail(dto.getEmail()));
    }
}
