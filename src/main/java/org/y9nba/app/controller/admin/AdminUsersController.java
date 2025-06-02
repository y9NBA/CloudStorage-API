package org.y9nba.app.controller.admin;

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
import org.y9nba.app.dao.entity.User;
import org.y9nba.app.dao.entity.Warning;
import org.y9nba.app.dto.response.Response;
import org.y9nba.app.dto.search.UserInfoDto;
import org.y9nba.app.dto.warning.WarningUserDto;
import org.y9nba.app.mapper.GeneralMapper;
import org.y9nba.app.service.impl.admin.BanServiceImpl;
import org.y9nba.app.service.impl.admin.WarningServiceImpl;
import org.y9nba.app.service.impl.user.UserSearchServiceImpl;
import org.y9nba.app.service.impl.user.UserServiceImpl;

import java.util.Set;
import java.util.UUID;

@Tag(
        name = "Admin Users Controller",
        description = "Модерация пользователей, бан и разбан пользователей, выдача и удаление предупреждений"
)
@RestController
@RequestMapping("/admin")
@ApiResponses({
        @ApiResponse(
                responseCode = "403",
                description = "Недостаточно прав для доступа",
                content = @Content(
                        mediaType = "application/json",
                        schema = @Schema(implementation = Response.class),
                        examples = @ExampleObject(value = "{\"message\": \"Недостаточно прав\"}")
                )
        ),
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
public class AdminUsersController {

    private final UserServiceImpl userService;
    private final UserSearchServiceImpl userSearchService;
    private final BanServiceImpl banService;
    private final WarningServiceImpl warningService;

    public AdminUsersController(UserServiceImpl userService, UserSearchServiceImpl userSearchService, BanServiceImpl banService, WarningServiceImpl warningService) {
        this.userService = userService;
        this.userSearchService = userSearchService;
        this.banService = banService;
        this.warningService = warningService;
    }

    @GetMapping("/list/users")
    @PreAuthorize("hasAuthority('INFO_USERS')")
    @Operation(
            summary = "Получить информацию о всех пользователях",
            description = "Возвращает информацию о всех пользователях, включая забаненных, если указан соответствующий параметр."
    )
    @ApiResponse(
            responseCode = "200",
            description = "Информация о пользователях успешно получена",
            content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = UserInfoDto.class)
            )
    )
    public Set<UserInfoDto> getAllUsersInfo(
            @RequestParam(required = false) UUID bucketName,
            @RequestParam(required = false) String email,
            @RequestParam(required = false) String username,
            @RequestParam(required = false, defaultValue = "false") boolean isBanned,
            @AuthenticationPrincipal User user) {

        if (isBanned) {
            return GeneralMapper.toUserInfoDto(
                    userSearchService.getAllBannedUsers(username, email, bucketName, user.getId())
            );
        } else {
            return GeneralMapper.toUserInfoDto(
                    userSearchService.getAllActiveUsers(username, email, bucketName, user.getId())
            );
        }
    }

    @GetMapping("/info/user/{userId}")
    @PreAuthorize("hasAuthority('INFO_USERS')")
    @Operation(
            summary = "Получить информацию о пользователе по ID",
            description = "Возвращает информацию о пользователе по его уникальному идентификатору."
    )
    @ApiResponse(
            responseCode = "200",
            description = "Информация о пользователе успешно получена",
            content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = UserInfoDto.class)
            )
    )
    public UserInfoDto getUserInfoById(@PathVariable Long userId) {
        return new UserInfoDto(userService.getById(userId));
    }

    @GetMapping("/info/user/{userId}/warnings")
    @PreAuthorize("hasAuthority('INFO_USERS')")
    @Operation(
            summary = "Получить предупреждения пользователя",
            description = "Возвращает активные и неактивные предупреждения пользователя по его ID."
    )
    @ApiResponse(
            responseCode = "200",
            description = "Предупреждения пользователя успешно получены",
            content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = WarningUserDto.class)
            )
            )
    public WarningUserDto getUserWarnings(@PathVariable Long userId) {
        Set<Warning> activeWarnings = warningService.getAllActiveWarningsByUserId(userId);
        Set<Warning> otherWarnings = warningService.getAllWarningsByUserId(userId);

        otherWarnings.removeAll(activeWarnings);

        return new WarningUserDto(
                GeneralMapper.toWarningDto(activeWarnings),
                GeneralMapper.toWarningDto(otherWarnings)
        );
    }

    @DeleteMapping("/warnings/revoke/{userId}")
    @PreAuthorize("hasAuthority('BAN_HAMMER')")
    @Operation(
            summary = "Снять одно предупреждение пользователя",
            description = "Снимает одно активное предупреждение у пользователя по его ID."
    )
    @ApiResponse(
            responseCode = "200",
            description = "Предупреждение успешно снято",
            content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = Response.class)
            )
    )
    public Response revokeOneActiveWarningByUserId(@PathVariable Long userId) {
        warningService.revokeWarning(userId);
        return new Response("Одно предупреждение пользователя было снято");
    }

    @DeleteMapping("/warnings/revoke/all/{userId}")
    @PreAuthorize("hasAuthority('BAN_HAMMER')")
    @Operation(
            summary = "Снять все предупреждения пользователя",
            description = "Снимает все активные предупреждения у пользователя по его ID."
    )
    @ApiResponse(
            responseCode = "200",
            description = "Все предупреждения успешно сняты",
            content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = Response.class)
            )
    )
    public Response revokeAllActiveWarningsByUserId(@PathVariable Long userId) {
        warningService.revokeAllWarnings(userId);
        return new Response("Все предупреждения пользователя были сняты");
    }

    @PutMapping("/ban/user/{userId}")
    @PreAuthorize("hasAuthority('BAN_HAMMER')")
    @Operation(
            summary = "Забанить пользователя",
            description = "Забанивает пользователя по его ID."
    )
    @ApiResponse(
            responseCode = "200",
            description = "Пользователь успешно забанен",
            content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = Response.class)
            )
    )
    public Response banUserById(@PathVariable Long userId) {
        banService.banUser (userId);
        return new Response("Пользователь забанен");
    }

    @PutMapping("/unban/user/{userId}")
    @PreAuthorize("hasAuthority('BAN_HAMMER')")
    @Operation(
            summary = "Разбанить пользователя",
            description = "Разбанивает пользователя по его ID."
    )
    @ApiResponse(
            responseCode = "200",
            description = "Пользователь успешно разбанен",
            content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = Response.class)
            )
    )
    public Response unbanUserById(@PathVariable Long userId) {
        banService.unbanUser (userId);
        return new Response("Пользователь разбанен");
    }
}
