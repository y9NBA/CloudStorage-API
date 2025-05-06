package org.y9nba.app.controller.fileaccess;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.y9nba.app.dto.fileaccess.FileAccessGiveRequestDto;
import org.y9nba.app.dto.response.Response;
import org.y9nba.app.model.FileModel;
import org.y9nba.app.model.UserModel;
import org.y9nba.app.service.impl.FileStorageServiceImpl;
import org.y9nba.app.service.impl.UserServiceImpl;

@RestController
@RequestMapping("/access")
public class FileAccessController {

    private final FileStorageServiceImpl fileStorageService;
    private final UserServiceImpl userService;

    public FileAccessController(FileStorageServiceImpl fileStorageService, UserServiceImpl userService) {
        this.fileStorageService = fileStorageService;
        this.userService = userService;
    }

    @PostMapping("/give")
    public Response giveAccess(@RequestParam String fileName, @RequestParam(required = false) String folderUrl, @RequestBody FileAccessGiveRequestDto fileAccessGiveRequestDto, @AuthenticationPrincipal UserModel userModel) {
        UserModel collaboratorUser = userService.getById(fileAccessGiveRequestDto.getCollaboratorId());
        FileModel fileModel = fileStorageService.giveAccessOnFileForUser(
                userModel.getId(),
                fileName,
                folderUrl,
                fileAccessGiveRequestDto.getCollaboratorId(),
                fileAccessGiveRequestDto.extractAccessLevel()
        );

        return new Response("Пользователю " + collaboratorUser.getUsername() + " дан доступ " + fileAccessGiveRequestDto.extractAccessLevel().toString() + " к файлу " + fileModel.getUrl());
    }

    @DeleteMapping("/revoke")
    public Response revokeAccess(@RequestParam String fileName, @RequestParam(required = false) String folderUrl, @RequestParam Long collaboratorUserId, @AuthenticationPrincipal UserModel userModel) {
        UserModel collaboratorUser = userService.getById(collaboratorUserId);
        FileModel fileModel = fileStorageService.revokeAccessOnFileForUser(
                userModel.getId(),
                fileName,
                folderUrl,
                collaboratorUserId
        );

        return new Response("Пользовтелю " + collaboratorUser.getUsername() + " отключен доступ к файлу " + fileModel.getUrl());
    }

    @DeleteMapping("/revoke/all")
    public Response revokeAllAccess(@RequestParam String fileName, @RequestParam(required = false) String folderUrl, @AuthenticationPrincipal UserModel userModel) {
        FileModel fileModel = fileStorageService.revokeAllAccessOnFile(
                userModel.getId(),
                fileName,
                folderUrl
        );

        return new Response("Для всех пользователей отключен доступ к файлу " + fileModel.getUrl());
    }

    @PutMapping("/open/file")
    public Response makeFileIsOpen(@RequestParam String fileName, @RequestParam(required = false) String folderUrl, @AuthenticationPrincipal UserModel userModel) {
        FileModel fileModel = fileStorageService.makeFilePublic(userModel.getId(), fileName, folderUrl);
        return new Response("Все имеют доступ на чтение к файлу " + fileModel.getUrl());
    }

    @PutMapping("/close/file")
    public Response makeFileIsClose(@RequestParam String fileName, @RequestParam(required = false) String folderUrl, @AuthenticationPrincipal UserModel userModel) {
        FileModel fileModel = fileStorageService.makeFilePrivate(userModel.getId(), fileName, folderUrl);
        return new Response("Никто не имеет доступа на чтение к файлу " + fileModel.getUrl());
    }
}
