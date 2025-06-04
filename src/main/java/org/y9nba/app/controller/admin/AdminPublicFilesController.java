package org.y9nba.app.controller.admin;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.y9nba.app.dao.entity.User;
import org.y9nba.app.dto.file.FilePresentDto;
import org.y9nba.app.dto.response.Response;
import org.y9nba.app.dto.search.UserInfoDto;
import org.y9nba.app.mapper.GeneralMapper;
import org.y9nba.app.service.impl.admin.ModerationServiceImpl;
import org.y9nba.app.service.impl.admin.PublicFilesServiceImpl;

import java.util.Set;

@Tag(
        name = "Admin Public Files Controller",
        description = "Модерация публичных файлов"
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
public class AdminPublicFilesController {

    private final PublicFilesServiceImpl publicFilesService;
    private final ModerationServiceImpl moderationService;

    public AdminPublicFilesController(PublicFilesServiceImpl publicFilesService, ModerationServiceImpl moderationService) {
        this.publicFilesService = publicFilesService;
        this.moderationService = moderationService;
    }

    @PreAuthorize("hasAuthority('VIEW_PUBLIC_FILES')")
    @Operation(
            summary = "Получить все публичные файлы",
            description = "Возвращает список всех публичных файлов или файлов конкретного автора, если указан authorId."
    )
    @ApiResponse(
            responseCode = "200",
            description = "Список публичных файлов",
            content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = FilePresentDto.class)
            )
    )
    @GetMapping("/list/public/files")
    public Set<FilePresentDto> getAllPublicFiles(@RequestParam(name = "authorId", required = false) Long authorId) {
        if (authorId != null) {
            return GeneralMapper.toFilePresentDto(
                    publicFilesService.getPublicFilesByUserId(authorId)
            );
        } else {
            return GeneralMapper.toFilePresentDto(
                    publicFilesService.getAllPublicFiles()
            );
        }
    }

    @PreAuthorize("hasAuthority('VIEW_PUBLIC_FILES')")
    @Operation(
            summary = "Просмотр публичного файла",
            description = "Возвращает содержимое публичного файла по его ID."
    )
    @ApiResponse(
            responseCode = "200",
            description = "Содержимое публичного файла"
    )
    @GetMapping("/view/public/file")
    public ResponseEntity<InputStreamResource> getPublicFile(@RequestParam(name = "id") Long fileId) {
        return publicFilesService.viewPublicFile(fileId);
    }

    @PreAuthorize("hasAuthority('VIEW_PUBLIC_FILES')")
    @Operation(
            summary = "Получить автора публичного файла",
            description = "Возвращает информацию об авторе публичного файла по его ID."
    )
    @ApiResponse(
            responseCode = "200",
            description = "Информация об авторе публичного файла",
            content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = UserInfoDto.class)
            )
    )
    @GetMapping("/author/public/file")
    public UserInfoDto getAuthorPublicFile(@RequestParam(name = "id") Long fileId) {
        return new UserInfoDto(publicFilesService.getAuthorOfPublicFileById(fileId));
    }

    @PreAuthorize("hasAuthority('REVOKE_PUBLIC_FILES')")
    @Operation(
            summary = "Отозвать публичный файл",
            description = "Делает публичный файл приватным и уведомляет автора о данном действии. Также, если warning=true, автору выдается предупреждение."
    )
    @ApiResponse(
            responseCode = "200",
            description = "Файл больше не публичный.",
            content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = Response.class)
            )
    )
    @DeleteMapping("/revoke/file")
    public Response revokePublicFile(@RequestParam(name = "id") Long fileId, @RequestParam(name = "warning") boolean warning, @AuthenticationPrincipal User user) {
        String msg = "Файл больше не публичный.";

        if (warning) {
            moderationService.revokePublicFileWithWarning(fileId, user.getId());
            return new Response(msg + " " + "Автору файла выдано предупреждение.");
        } else {
            moderationService.revokePublicFileWithNotification(fileId);
            return new Response(msg);
        }
    }
}
