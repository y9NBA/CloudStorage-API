package org.y9nba.app.controller.file;

import org.springframework.core.io.InputStreamResource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.y9nba.app.dto.file.FileDto;
import org.y9nba.app.dto.file.FileInputStreamWithAccessDto;
import org.y9nba.app.mapper.GeneralMapper;
import org.y9nba.app.model.FileModel;
import org.y9nba.app.model.UserModel;
import org.y9nba.app.service.impl.FileStorageServiceImpl;
import org.y9nba.app.service.impl.UserServiceImpl;

import java.io.InputStream;
import java.util.Set;

@RestController
@RequestMapping("/sharing")
public class FileSharedController {
    private final FileStorageServiceImpl fileStorageService;

    public FileSharedController(FileStorageServiceImpl fileStorageService) {
        this.fileStorageService = fileStorageService;
    }

    @GetMapping("/download/file")
    public ResponseEntity<InputStreamResource> getSharedFile(@RequestParam String bucketName, @RequestParam String fileName, @RequestParam(required = false) String folderUrl, @AuthenticationPrincipal UserModel userModel) {
        FileInputStreamWithAccessDto dto = fileStorageService.downloadFileByAccess(
                userModel == null ? null : userModel.getId(),
                bucketName,
                fileName,
                folderUrl
        );

        FileModel fileModel = fileStorageService.findFile(bucketName, fileName, folderUrl);

        return fileStorageService.getResourceForDownloadByInputStream(dto, fileModel);
    }

    @GetMapping("/view/file")
    public ResponseEntity<InputStreamResource> getSharedViewFile(@RequestParam String bucketName, @RequestParam String fileName, @RequestParam(required = false) String folderUrl, @AuthenticationPrincipal UserModel userModel) {
        FileInputStreamWithAccessDto dto = fileStorageService.downloadFileByAccess(
                userModel == null ? null : userModel.getId(),
                bucketName,
                fileName,
                folderUrl
        );

        FileModel fileModel = fileStorageService.findFile(bucketName, fileName, folderUrl);

        return fileStorageService.getResourceForViewByInputStream(dto, fileModel);
    }

    @PostMapping(path = "/update/file", consumes = {MediaType.MULTIPART_FORM_DATA_VALUE})
    public FileDto updateSharedFile(@RequestPart(value = "file") MultipartFile file, @RequestParam String bucketName, @RequestParam String fileName, @RequestParam(required = false) String folderUrl, @AuthenticationPrincipal UserModel userModel) {
        FileModel updatedFile = fileStorageService.uploadFileByAccess(userModel.getId(), file, bucketName, fileName, folderUrl);
        return new FileDto(updatedFile);
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
    public FileDto getOwnerFile(@RequestParam String bucketName, @RequestParam String fileName, @RequestParam(required = false) String folderUrl, @AuthenticationPrincipal UserModel userModel) {
        return new FileDto(fileStorageService.findOwnerByFileId(userModel.getId(), bucketName, fileName, folderUrl));
    }
}
