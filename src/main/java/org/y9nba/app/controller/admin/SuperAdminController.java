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
import org.y9nba.app.dto.response.Response;
import org.y9nba.app.dto.search.UserInfoDto;
import org.y9nba.app.dto.user.UserCreateDto;
import org.y9nba.app.mapper.GeneralMapper;
import org.y9nba.app.dao.entity.User;
import org.y9nba.app.service.impl.user.UserSearchServiceImpl;
import org.y9nba.app.service.impl.user.UserServiceImpl;

import java.util.Set;
import java.util.UUID;

@Tag(
        name = "Super Admin Controller",
        description = "Изменения и просмотр админского состава"
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
public class SuperAdminController {

    private final UserServiceImpl userService;
    private final UserSearchServiceImpl userSearchService;

    public SuperAdminController(UserServiceImpl userService, UserSearchServiceImpl userSearchService) {
        this.userService = userService;
        this.userSearchService = userSearchService;
    }

    @GetMapping("/list/admins")
    @PreAuthorize("hasAuthority('INFO_ADMINS')")
    @Operation(
            summary = "Получить информацию о всех администраторах",
            description = "Возвращает информацию о всех администраторах в системе."
    )
    @ApiResponse(
            responseCode = "200",
            description = "Информация о администраторах успешно получена",
            content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = UserInfoDto.class)
            )
    )
    public Set<UserInfoDto> getAllAdminsInfo(@RequestParam(required = false) UUID bucketName, @RequestParam(required = false) String email, @RequestParam(required = false) String username, @AuthenticationPrincipal User user) {
        return GeneralMapper.toUserInfoDto(
                userSearchService.getAllAdmins(username, email, bucketName, user.getId())
        );
    }

    @GetMapping("/info/admin")
    @PreAuthorize("hasAuthority('INFO_ADMINS')")
    @Operation(
            summary = "Получить информацию об администраторе",
            description = "Возвращает информацию об администраторе по его ID."
    )
    @ApiResponse(
            responseCode = "200",
            description = "Информация об администраторе успешно получена",
            content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = UserInfoDto.class)
            )
    )
    public UserInfoDto getAdminInfo(@RequestParam(name = "id") Long id) {
        return new UserInfoDto(userSearchService.getAdminById(id));
    }

    @PostMapping("/create/admin")
    @PreAuthorize("hasAuthority('CREATE_ADMIN')")
    @Operation(
            summary = "Создать нового администратора",
            description = "Создает нового администратора в системе."
    )
    @ApiResponse(
            responseCode = "200",
            description = "Администратор успешно создан",
            content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = UserInfoDto.class)
            )
    )
    public UserInfoDto createNewAdmin(@RequestBody UserCreateDto dto) {
        return new UserInfoDto(userService.createAdmin(dto));
    }

    @PostMapping("/delete/admin")
    @PreAuthorize("hasAuthority('DELETE_ADMIN')")
    @Operation(
            summary = "Удалить администратора",
            description = "Удаляет администратора по его ID."
    )
    @ApiResponse(
            responseCode = "200",
            description = "Админ успешно удален",
            content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = Response.class)
            )
    )
    public Response deleteAdmin(@RequestParam(name = "id") Long adminId) {
        userService.deleteById(adminId);
        return new Response("Админ успешно удален");
    }
}
