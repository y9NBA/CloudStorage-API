package org.y9nba.app.controller.user;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.y9nba.app.dto.response.Response;
import org.y9nba.app.dto.search.UserSearchDto;
import org.y9nba.app.dto.user.UserProfileDto;
import org.y9nba.app.mapper.GeneralMapper;
import org.y9nba.app.dao.entity.User;
import org.y9nba.app.service.impl.user.UserSearchServiceImpl;
import org.y9nba.app.service.impl.user.UserServiceImpl;

import java.util.Set;
import java.util.UUID;

@Tag(
        name = "User  Search Controller",
        description = "Поиск других пользователей"
)
@RestController
@RequestMapping("/user/search")
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
public class UserSearchController {

    private final UserServiceImpl userService;
    private final UserSearchServiceImpl userSearchService;

    public UserSearchController(UserServiceImpl userService, UserSearchServiceImpl userSearchService) {
        this.userService = userService;
        this.userSearchService = userSearchService;
    }

    @GetMapping
    @Operation(summary = "Получить всех пользователей")
    @ApiResponse(
            responseCode = "200",
            description = "Список пользователей с краткой информацией о них",
            content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = UserSearchDto.class)
            )
    )
    public Set<UserSearchDto> getAllUsers(@RequestParam(required = false) UUID bucketName, @RequestParam(required = false) String email, @RequestParam(required = false) String username, @AuthenticationPrincipal User user) {
        return GeneralMapper.toUserSearchDto(
                userSearchService.getAllUsers(username, email, bucketName, user.getId())
        );
    }

    @GetMapping("/{id}")
    @Operation(summary = "Получить определенного пользователя по ID")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Пользователь с краткой информацией или профиль пользователя, который отправил запрос",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = UserProfileDto.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Пользователь не найден",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = Response.class)
                    )
            )
    })
    public Object getUserById(@PathVariable Long id, @AuthenticationPrincipal User user) {
        if (user.getId().equals(id)) {
            return new UserProfileDto(user);
        } else {
            return new UserSearchDto(userService.getById(id));
        }
    }
}
