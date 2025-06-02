package org.y9nba.app.controller.user;

import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.y9nba.app.constant.Role;
import org.y9nba.app.dao.entity.Session;
import org.y9nba.app.dto.response.ErrorResponse;
import org.y9nba.app.dto.response.Response;
import org.y9nba.app.dto.session.AllSessionsDto;
import org.y9nba.app.dto.session.SessionDto;
import org.y9nba.app.dto.user.UserProfileDto;
import org.y9nba.app.dao.entity.User;
import org.y9nba.app.dto.user.UserRoleDto;
import org.y9nba.app.exception.web.session.NotUseRevokeSessionOnCurrentSession;
import org.y9nba.app.mapper.GeneralMapper;
import org.y9nba.app.service.face.token.SessionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;

import java.util.Set;
import java.util.UUID;

@Tag(
        name = "User Info Controller",
        description = "Информация о пользователе"
)
@RestController
@RequestMapping("/user/info")
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
public class UserInfoController {

    private final SessionService sessionService;

    public UserInfoController(SessionService sessionService) {
        this.sessionService = sessionService;
    }

    @Operation(summary = "Получить информацию о профиле")
    @ApiResponse(
            responseCode = "200",
            description = "Успешно получена информация о профиле",
            content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = UserProfileDto.class)
            )
    )
    @GetMapping("/profile")
    public UserProfileDto getUserProfileInfo(@AuthenticationPrincipal User user) {
        return new UserProfileDto(user);
    }

    @GetMapping("/role")
    @Operation(summary = "Получить роль")
    @ApiResponse(
            responseCode = "200",
            description = "Роль пользователя",
            content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = UserRoleDto.class)
            )
    )
    public UserRoleDto getUserRole(@AuthenticationPrincipal User user) {
        return new UserRoleDto(user.getRole());
    }

    @GetMapping("/sessions")
    @Operation(summary = "Получить все сеансы")
    @ApiResponse(
            responseCode = "200",
            description = "Текущий сеанс и список других сеансов",
            content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = AllSessionsDto.class)
            )
    )
    public AllSessionsDto getAllSessions(@AuthenticationPrincipal User user, HttpServletRequest request) {
        Session currentSession = sessionService.getSessionByUserIdAndRequest(user.getId(), request);
        Set<Session> otherSessions = sessionService.getAllSessionsByUserIdExceptCurrent(user.getId(), currentSession.getId());

        return new AllSessionsDto(
                new SessionDto(currentSession),
                GeneralMapper.toSessionDto(otherSessions)
        );
    }

    @DeleteMapping("/sessions/revoke")
    @Operation(summary = "Завершить определенный сеанс по ID, но не текущий")
    @ApiResponse(
            responseCode = "200",
            description = "Сеанс завершен",
            content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = Response.class)
            )
    )
    public Response revokeSessionById(@RequestParam("sessionId") UUID sessionId, @AuthenticationPrincipal User user, HttpServletRequest request) {
        Session currentSession = sessionService.getSessionByUserIdAndRequest(user.getId(), request);

        if (currentSession.getId().equals(sessionId)) {
            throw new NotUseRevokeSessionOnCurrentSession();
        }

        sessionService.revokeSession(sessionId);

        return new Response("Сеанс завершен");
    }

    @DeleteMapping("/sessions/revoke/all")
    @Operation(summary = "Завершить все сеансы, кроме текущего")
    @ApiResponse(
            responseCode = "200",
            description = "Все сеансы завершены",
            content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = Response.class)
            )
    )
    public Response revokeAllSessions(@AuthenticationPrincipal User user, HttpServletRequest request) {
        Session currentSession = sessionService.getSessionByUserIdAndRequest(user.getId(), request);

        sessionService.revokeAllSessionsExceptCurrent(user.getId(), currentSession.getId());

        return new Response("Все сеансы завершены");
    }
}
