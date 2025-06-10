package org.y9nba.app.controller.fileaccess;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.y9nba.app.dto.fileaccess.FileAccessGiveRequestDto;
import org.y9nba.app.dto.response.Response;
import org.y9nba.app.dao.entity.File;
import org.y9nba.app.dao.entity.User;
import org.y9nba.app.service.impl.file.FileStorageServiceImpl;
import org.y9nba.app.service.impl.user.UserSearchServiceImpl;
import org.y9nba.app.service.impl.user.UserServiceImpl;

@Tag(
        name = "File Access Controller",
        description = "Управление доступом к файлам"
)
@RestController
@RequestMapping("/access")
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
public class FileAccessController {

    private final FileStorageServiceImpl fileStorageService;
    private final UserServiceImpl userService;
    private final UserSearchServiceImpl userSearchService;

    public FileAccessController(FileStorageServiceImpl fileStorageService, UserServiceImpl userService, UserSearchServiceImpl userSearchService) {
        this.fileStorageService = fileStorageService;
        this.userService = userService;
        this.userSearchService = userSearchService;
    }

    @PostMapping("/give")
    @Operation(summary = "Предоставить доступ к файлу определенному пользователю")
    @ApiResponse(
            responseCode = "200",
            description = "Пользователь получил доступ к файлу",
            content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = Response.class)
            )
    )
    public Response giveAccess(@RequestParam String fileName, @RequestParam(required = false) String folderUrl, @RequestBody FileAccessGiveRequestDto fileAccessGiveRequestDto, @AuthenticationPrincipal User user) {
        File file = fileStorageService.giveAccessOnFileForUser(
                user.getId(),
                fileName,
                folderUrl,
                fileAccessGiveRequestDto.getCollaboratorId(),
                fileAccessGiveRequestDto.extractAccessLevel()
        );

        User collaboratorUser = userSearchService.getUserById(fileAccessGiveRequestDto.getCollaboratorId());

        return new Response("Пользователю " + collaboratorUser.getUsername() + " дан доступ " + fileAccessGiveRequestDto.extractAccessLevel().toString() + " к файлу " + file.getUrl());
    }

    @DeleteMapping("/revoke")
    @Operation(summary = "Отключить доступ к файлу определенному пользователю")
    @ApiResponse(
            responseCode = "200",
            description = "Пользователь больше не имеет доступа к файлу",
            content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = Response.class)
            )
    )
    public Response revokeAccess(@RequestParam String fileName, @RequestParam(required = false) String folderUrl, @RequestParam Long collaboratorUserId, @AuthenticationPrincipal User user) {
        File file = fileStorageService.revokeAccessOnFileForUser(
                user.getId(),
                fileName,
                folderUrl,
                collaboratorUserId
        );

        User collaboratorUser = userSearchService.getUserById(collaboratorUserId);

        return new Response("Пользователю " + collaboratorUser.getUsername() + " отключен доступ к файлу " + file.getUrl());
    }

    @DeleteMapping("/revoke/all")
    @Operation(summary = "Отключить доступ к файлу всем пользователям, которым он был предоставлен")
    @ApiResponse(
            responseCode = "200",
            description = "Доступ отключен всем пользователям, которым был предоставлен доступ к файлу",
            content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = Response.class)
            )
    )
    public Response revokeAllAccess(@RequestParam String fileName, @RequestParam(required = false) String folderUrl, @AuthenticationPrincipal User user) {
        File file = fileStorageService.revokeAllAccessOnFile(
                user.getId(),
                fileName,
                folderUrl
        );

        return new Response("Для всех пользователей отключен доступ к файлу " + file.getUrl());
    }

    @PutMapping("/open/file")
    @Operation(summary = "Сделать файл публичным")
    @ApiResponse(
            responseCode = "200",
            description = "Файл могут читать все, у кого есть ссылка",
            content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = Response.class)
            )
    )
    public Response makeFileIsOpen(@RequestParam String fileName, @RequestParam(required = false) String folderUrl, @AuthenticationPrincipal User user) {
        File file = fileStorageService.makeFilePublic(user.getId(), fileName, folderUrl);
        return new Response("Все имеют доступ на чтение к файлу " + file.getUrl());
    }

    @PutMapping("/close/file")
    @Operation(summary = "Сделать файл приватным")
    @ApiResponse(
            responseCode = "200",
            description = "Доступ к файлу закрыт для всех",
            content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = Response.class)
            )
    )
    public Response makeFileIsClose(@RequestParam String fileName, @RequestParam(required = false) String folderUrl, @AuthenticationPrincipal User user) {
        File file = fileStorageService.makeFilePrivate(user.getId(), fileName, folderUrl);
        return new Response("Никто не имеет доступа на чтение к файлу " + file.getUrl());
    }
}
