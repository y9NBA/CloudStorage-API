package org.y9nba.app.controller.file;

import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.y9nba.app.dto.file.FileDto;
import org.y9nba.app.mapper.GeneralMapper;
import org.y9nba.app.model.FileModel;
import org.y9nba.app.model.UserModel;
import org.y9nba.app.service.impl.FileStorageServiceImpl;
import org.y9nba.app.service.impl.StorageServiceImpl;

import java.io.InputStream;
import java.util.List;
import java.util.Set;

@Tag(
        name = "File Storage Controller",
        description = "Взимодействие с файлами"
)
@RestController
@RequestMapping("/storage")
public class FileStorageController {

    private final StorageServiceImpl storageService;
    private final FileStorageServiceImpl fileStorageService;

    public FileStorageController(StorageServiceImpl storageService, FileStorageServiceImpl fileStorageService) {
        this.storageService = storageService;
        this.fileStorageService = fileStorageService;
    }

    @GetMapping(path = "/buckets")
    public List<String> listBuckets() {
        return storageService.getAllBucketsName();
    }

    @GetMapping(path = "/my-files")
    public Set<FileDto> getListFiles(@RequestParam(required = false) String folderUrl, @AuthenticationPrincipal UserModel userModel) {
        if (folderUrl != null) {
            return GeneralMapper.toFileDto(fileStorageService.findByUserIdAndFolderUrl(userModel.getId(), folderUrl));
        } else {
            return GeneralMapper.toFileDto(fileStorageService.findByUserId(userModel.getId()));
        }
    }

    @GetMapping(path = "/my-files/file")
    public FileDto getFile(@RequestParam String fileName, @RequestParam(required = false) String folderUrl, @AuthenticationPrincipal UserModel userModel) {
        return new FileDto(fileStorageService.findFile(userModel.getId(), fileName, folderUrl));
    }

    @PostMapping(path = "/upload", consumes = {MediaType.MULTIPART_FORM_DATA_VALUE})
    public FileDto uploadFile(@RequestPart(value = "file") MultipartFile file, @RequestParam(required = false) String folderUrl, @AuthenticationPrincipal UserModel userModel) {
        FileModel fileModel = fileStorageService.uploadFile(userModel.getId(), file, folderUrl);
        return new FileDto(fileModel);
    }

    @GetMapping(path = "/download/file")
    public ResponseEntity<InputStreamResource> downloadFile(@RequestParam String fileName, @RequestParam(required = false) String folderUrl, @AuthenticationPrincipal UserModel userModel) {
        InputStream inputStream = fileStorageService.downloadFile(userModel.getId(), fileName, folderUrl);
        return fileStorageService.getResourceByInputStream(inputStream, fileName);
    }
}
