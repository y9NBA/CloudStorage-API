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
        name = "File Storage Controller",
        description = "Взаимодействие с файлами"
)
@RestController
@RequestMapping("/storage")
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
public class FileStorageController {

    private final FileStorageServiceImpl fileStorageService;

    public FileStorageController(FileStorageServiceImpl fileStorageService) {
        this.fileStorageService = fileStorageService;
    }

    @GetMapping(path = "/my-files")
    @Operation(summary = "Получить список файлов пользователя")
    @ApiResponse(
            responseCode = "200",
            description = "Список файлов с краткой информацией",
            content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = FilePresentDto.class)
            )
    )
    public Set<FilePresentDto> getListFiles(@RequestParam(required = false) String folderUrl, @AuthenticationPrincipal User user) {
        fileStorageService.refreshFiles(user.getId());

        if (folderUrl != null) {
            return GeneralMapper.toFilePresentDto(
                    fileStorageService.findByUserIdAndFolderUrl(user.getId(), folderUrl)
            );
        } else {
            return GeneralMapper.toFilePresentDto(
                    fileStorageService.findByUserId(user.getId())
            );
        }
    }

    @GetMapping(path = "/my-files/file")
    @Operation(summary = "Получить файл")
    @ApiResponse(
            responseCode = "200",
            description = "Файл с полной информацией",
            content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = FileDto.class)
            )
    )
    public FileDto getFile(@RequestParam String fileName, @RequestParam(required = false) String folderUrl, @AuthenticationPrincipal User user) {
        return new FileDto(fileStorageService.findFile(user.getId(), fileName, folderUrl));
    }

    @PostMapping(path = "/upload", consumes = {MediaType.MULTIPART_FORM_DATA_VALUE})
    @Operation(summary = "Загрузить файл")
    @ApiResponse(
            responseCode = "200",
            description = "Краткая информация о загруженном файле",
            content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = FilePresentDto.class)
            )
    )
    public FilePresentDto uploadFile(@RequestPart(value = "file") MultipartFile file, @RequestParam(required = false) String folderUrl, @AuthenticationPrincipal User user) {
        return new FilePresentDto(fileStorageService.uploadFile(user.getId(), file, folderUrl));
    }

    @PostMapping(path = "/upload/folder")
    @Operation(summary = "Загрузить папку целиком")
    @ApiResponse(
            responseCode = "200",
            description = "Список файлов этой папки с краткой информацией",
            content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = FilePresentDto.class)
            )
    )
    public Set<FilePresentDto> uploadFolder(@RequestParam String folderPath, @RequestParam(required = false) String folderUrl, @AuthenticationPrincipal User user) {
        return null;    // TODO: доделать
    }

    @GetMapping(path = "/download/file")
    @Operation(summary = "Скачать файл")
    @ApiResponse(
            responseCode = "200",
            description = "Файл скачан"
    )
    public ResponseEntity<InputStreamResource> downloadFile(@RequestParam String fileName, @RequestParam(required = false) String folderUrl, @AuthenticationPrincipal User user) {
        FileInputStreamWithAccessDto dto = fileStorageService.downloadFile(user.getId(), fileName, folderUrl);
        File file = fileStorageService.findFile(user.getId(), fileName, folderUrl);
        return fileStorageService.getResourceForDownloadByInputStream(dto, file);
    }

    @PostMapping(path = "/download/folder")
    @Operation(summary = "Скачать папку целиком архивом")
    @ApiResponse(
            responseCode = "200",
            description = "Папка в виде архива скачана"
    )
    public Object downloadFolder(@RequestParam(required = false) String folderUrl, @AuthenticationPrincipal User user) {
        return null;    // TODO: доделать
    }

    @GetMapping(path = "/view/file")
    @Operation(summary = "Просмотреть файл")
    @ApiResponse(
            responseCode = "200",
            description = "Просмотр содержимого файла"
    )
    public ResponseEntity<InputStreamResource> viewFile(@RequestParam String fileName, @RequestParam(required = false) String folderUrl, @AuthenticationPrincipal User user) {
        FileInputStreamWithAccessDto dto = fileStorageService.downloadFile(user.getId(), fileName, folderUrl);
        File file = fileStorageService.findFile(user.getId(), fileName, folderUrl);
        return fileStorageService.getResourceForViewByInputStream(dto, file);
    }

    @DeleteMapping(path = "/delete/file")
    @Operation(summary = "Удалить файл")
    @ApiResponse(
            responseCode = "200",
            description = "Файл успешно удален",
            content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = Response.class)
            )
    )
    public Response deleteFile(@RequestParam String fileName, @RequestParam(required = false) String folderUrl, @AuthenticationPrincipal User user) {
        String deleteFileUrl = fileStorageService.deleteFile(user.getId(), fileName, folderUrl);
        return new Response("Файл успешно удалён по пути: " + deleteFileUrl);
    }

    @DeleteMapping(path = "/delete/folder")
    @Operation(summary = "Удалить папку целиком")
    @ApiResponse(
            responseCode = "200",
            description = "Папка успешно удалена",
            content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = Response.class)
            )
    )
    public Response deleteFolder(@RequestParam String folderUrl, @AuthenticationPrincipal User user) {
        String deleteFolderUrl = fileStorageService.deleteFilesByFolder(user.getId(), folderUrl);
        return new Response("Папка успешно удалена по пути: " + deleteFolderUrl);
    }

    @PostMapping(path = "/move/file")
    @Operation(summary = "Переместить файл")
    @ApiResponse(
            responseCode = "200",
            description = "Файл с краткой информацией и обновлённым URL",
            content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = FilePresentDto.class)
            )
    )
    public FilePresentDto moveFile(@RequestParam String fileName, @RequestParam String newFolderUrl, @RequestParam(required = false) String oldFolderUrl, @AuthenticationPrincipal User user) {
        return new FilePresentDto(fileStorageService.moveFileOnNewUrl(user.getId(), fileName, newFolderUrl, oldFolderUrl));
    }

    @PostMapping(path = "/copy/file")
    @Operation(summary = "Создать копию файла")
    @ApiResponse(
            responseCode = "200",
            description = "Копия файла с краткой информацией",
            content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = FilePresentDto.class)
            )
    )
    public FilePresentDto copyFile(@RequestParam String fileName, @RequestParam(required = false) String folderUrl, @AuthenticationPrincipal User user) {
        return new FilePresentDto(fileStorageService.copyExistingFile(user.getId(), fileName, folderUrl));
    }

    @PostMapping(path = "/rename/file")
    @Operation(summary = "Переименовать файл")
    @ApiResponse(
            responseCode = "200",
            description = "Файл с краткой информацией и новым именем",
            content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = FilePresentDto.class)
            )
    )
    public FilePresentDto renameFile(@RequestParam String fileName, @RequestParam String newName, @RequestParam(required = false) String folderUrl, @AuthenticationPrincipal User user) {
        return new FilePresentDto(fileStorageService.renameFile(user.getId(), fileName, newName, folderUrl));
    }
}
