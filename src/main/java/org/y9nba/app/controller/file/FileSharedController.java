package org.y9nba.app.controller.file;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.y9nba.app.dto.file.FileDto;
import org.y9nba.app.dto.file.FileInputStreamWithAccessDto;
import org.y9nba.app.dto.file.FilePresentDto;
import org.y9nba.app.dto.response.Response;
import org.y9nba.app.mapper.GeneralMapper;
import org.y9nba.app.dao.entity.File;
import org.y9nba.app.dao.entity.User;
import org.y9nba.app.service.impl.file.FileStorageServiceImpl;

import java.util.Set;

@Tag(
        name = "File Shared Controller",
        description = "Просмотр и скачивание общих файлов и получение доступа к ним, изменение общих файлов"
)
@RestController
@RequestMapping("/sharing")
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
public class FileSharedController {
    private final FileStorageServiceImpl fileStorageService;

    public FileSharedController(FileStorageServiceImpl fileStorageService) {
        this.fileStorageService = fileStorageService;
    }

    @GetMapping("/download/file")
    @Operation(summary = "Скачать общий файла")
    @ApiResponse(
            responseCode = "200",
            description = "Общий файл скачан"
    )
    public ResponseEntity<InputStreamResource> getSharedFile(@RequestParam String bucketName, @RequestParam String fileName, @RequestParam(required = false) String folderUrl, @AuthenticationPrincipal User user) {
        FileInputStreamWithAccessDto dto = fileStorageService.downloadFileByAccess(
                user == null ? null : user.getId(),
                bucketName,
                fileName,
                folderUrl
        );

        File file = fileStorageService.findFile(bucketName, fileName, folderUrl);

        return fileStorageService.getResourceForDownloadByInputStream(dto, file);
    }

    @GetMapping("/view/file")
    @Operation(summary = "Просмотр общего файла")
    @ApiResponse(
            responseCode = "200",
            description = "Просмотр содержимого общего файла"
    )
    public ResponseEntity<InputStreamResource> getSharedViewFile(@RequestParam String bucketName, @RequestParam String fileName, @RequestParam(required = false) String folderUrl, @AuthenticationPrincipal User user) {
        FileInputStreamWithAccessDto dto = fileStorageService.downloadFileByAccess(
                user == null ? null : user.getId(),
                bucketName,
                fileName,
                folderUrl
        );

        File file = fileStorageService.findFile(bucketName, fileName, folderUrl);

        return fileStorageService.getResourceForViewByInputStream(dto, file);
    }

    @PostMapping(path = "/update/file", consumes = {MediaType.MULTIPART_FORM_DATA_VALUE})
    @Operation(summary = "Обновление общего файла")
    @ApiResponse(
            responseCode = "200",
            description = "Краткая информация об обновленном общем файле",
            content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = FilePresentDto.class)
            )
    )
    public FilePresentDto updateSharedFile(@RequestPart(value = "file") MultipartFile file, @RequestParam String bucketName, @RequestParam String fileName, @RequestParam(required = false) String folderUrl, @AuthenticationPrincipal User user) {
        File updatedFile = fileStorageService.uploadFileByAccess(user.getId(), file, bucketName, fileName, folderUrl);
        return new FilePresentDto(updatedFile);
    }

    @GetMapping("/owner-files")
    @Operation(summary = "Получить список общих файлов")
    @ApiResponse(
            responseCode = "200",
            description = "Список общих файлов с краткой информацией о них",
            content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = FilePresentDto.class)
            )
    )
    public Set<FilePresentDto> getOwnerFiles(@RequestParam(required = false) String folderUrl, @AuthenticationPrincipal User user) {
        if (folderUrl != null) {
            return GeneralMapper.toFilePresentDto(fileStorageService.findOwnerByUserIdAndFolderUrl(user.getId(), folderUrl));
        } else {
            return GeneralMapper.toFilePresentDto(fileStorageService.findOwnerByUserId(user.getId()));
        }
    }

    @GetMapping(path = "/owner-files/file")
    @Operation(summary = "Получить общий файл")
    @ApiResponse(
            responseCode = "200",
            description = "Полная информация об общем файле",
            content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = FileDto.class)
            )
    )
    public FileDto getOwnerFile(@RequestParam String bucketName, @RequestParam String fileName, @RequestParam(required = false) String folderUrl, @AuthenticationPrincipal User user) {
        return new FileDto(fileStorageService.findOwnerByBucketNameAndFileNameAndFolderUrl(user.getId(), bucketName, fileName, folderUrl));
    }
}
