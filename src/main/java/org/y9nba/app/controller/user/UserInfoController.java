package org.y9nba.app.controller.user;

import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.y9nba.app.dto.response.ErrorResponse;
import org.y9nba.app.dto.user.UserDto;
import org.y9nba.app.model.UserModel;
import org.y9nba.app.service.impl.UserServiceImpl;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;

@Tag(
        name = "UserInfo Controller",
        description = "Информация о пользователе"
)
@RestController
@RequestMapping("/user/info")
public class UserInfoController {

    private final UserServiceImpl userService;

    public UserInfoController(UserServiceImpl userService) {
        this.userService = userService;
    }

    @Operation(
            summary = "Получить информацию о профиле пользователя",
            description = "Возвращает информацию о текущем пользователя токену из запроса"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200",
                    description = "Успешно получена информация о профиле",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = UserDto.class)
                    )),
            @ApiResponse(responseCode = "401",
                    description = "Не авторизованный запрос",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class),
                            examples = @ExampleObject(value = "{\"message\": \"UNAUTHORIZED\"}")
                    ))
    })
    @GetMapping("/profile")
    public UserDto getUserProfileInfo(@AuthenticationPrincipal UserModel user) {
        return new UserDto(userService.getById(user.getId()));
    }
}