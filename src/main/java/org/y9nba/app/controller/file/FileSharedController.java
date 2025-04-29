package org.y9nba.app.controller.file;

import org.springframework.core.io.InputStreamResource;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.y9nba.app.dto.file.FileDto;
import org.y9nba.app.dto.share.ExpireRequestDto;
import org.y9nba.app.dto.share.SharedUrlResponseDto;
import org.y9nba.app.mapper.GeneralMapper;
import org.y9nba.app.model.UserModel;
import org.y9nba.app.service.impl.FileStorageServiceImpl;

import java.io.InputStream;
import java.util.Set;

@RestController
@RequestMapping("/sharing")
public class FileSharedController {
    private final FileStorageServiceImpl fileStorageService;

    public FileSharedController(FileStorageServiceImpl fileStorageService) {
        this.fileStorageService = fileStorageService;
    }

    @PostMapping("/file")
    public SharedUrlResponseDto getSharedUrlForFile(@RequestParam String fileName, @RequestParam(required = false) String folderUrl, @RequestBody ExpireRequestDto expireRequestDto, @AuthenticationPrincipal UserModel userModel) {
        return fileStorageService.getSharedUrlForFile(expireRequestDto, userModel.getId(), fileName, folderUrl);
    }

    @GetMapping("/file-access")
    public ResponseEntity<InputStreamResource> getFileByAccess(@RequestParam Long fileId, @AuthenticationPrincipal UserModel userModel) {
        InputStream inputStream = fileStorageService.downloadFileByAccess(userModel.getId(), fileId);
        String fileName = fileStorageService.findOwnerByFileId(userModel.getId(), fileId).getFileName();

        return fileStorageService.getResourceByInputStream(inputStream, fileName);
    }

    @GetMapping("/owner-files")
    public Set<FileDto> getOwnerFiles(@RequestParam(required = false) String folderUrl, @AuthenticationPrincipal UserModel userModel) {
        if (folderUrl != null) {
            return GeneralMapper.toFileDto(fileStorageService.findOwnerByUserIdAndFolderUrl(userModel.getId(), folderUrl));
        } else {
            return GeneralMapper.toFileDto(fileStorageService.findOwnerByUserId(userModel.getId()));
        }
    }

    @GetMapping(path = "/owner-files/file")
    public FileDto getFile(@RequestParam Long fileId, @AuthenticationPrincipal UserModel userModel) {
        return new FileDto(fileStorageService.findOwnerByFileId(userModel.getId(), fileId));
    }
}
