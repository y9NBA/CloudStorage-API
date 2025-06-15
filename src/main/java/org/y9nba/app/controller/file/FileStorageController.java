package org.y9nba.app.controller.file;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import org.simpleframework.xml.core.Validate;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.y9nba.app.dto.file.FileDto;
import org.y9nba.app.dto.file.FileInputStreamWithAccessDto;
import org.y9nba.app.dto.file.FilePresentDto;
import org.y9nba.app.dto.file.FolderDataDto;
import org.y9nba.app.dto.response.Response;
import org.y9nba.app.exception.web.file.FileInRequestIsEmptyException;
import org.y9nba.app.mapper.GeneralMapper;
import org.y9nba.app.dao.entity.File;
import org.y9nba.app.dao.entity.User;
import org.y9nba.app.service.impl.file.FileStorageServiceImpl;

import java.util.Set;
import java.util.stream.Collectors;

@Tag(
        name = "File Storage Controller",
        description = "Взаимодействие с файловым хранилищем"
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
    @Operation(summary = "Получить информацию о файл")
    @ApiResponse(
            responseCode = "200",
            description = "Полная информация о файле",
            content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = FileDto.class)
            )
    )
    public FileDto getFile(@RequestParam String fileName, @RequestParam(required = false) String folderUrl, @AuthenticationPrincipal User user) {
        return new FileDto(fileStorageService.findFile(user.getId(), fileName, folderUrl));
    }

    @GetMapping(path = "/my-folders")
    @Operation(summary = "Получить список всех папок")
    @ApiResponse(
            responseCode = "200",
            description = "Список папок и подкаталогов, в которых есть файлы",
            content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = String.class)
            )
    )
    public Set<String> getListFolders(@AuthenticationPrincipal User user) {
        Set<FilePresentDto> filePresentDtos = GeneralMapper.toFilePresentDto(
                fileStorageService.findByUserId(user.getId())
        );

        return filePresentDtos.stream().map(FilePresentDto::getFolderURL).collect(Collectors.toSet());
    }

    @GetMapping(path = "/my-folders/folder")
    @Operation(summary = "Получить информацию о папке")
    @ApiResponse(
            responseCode = "200",
            description = "Полная информация о папке",
            content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = FolderDataDto.class)
            )
    )
    public FolderDataDto getFolder(@RequestParam(required = false) String folderUrl, @AuthenticationPrincipal User user) {
        Set<File> files;

        if (folderUrl != null) {
            files = fileStorageService.findByUserIdAndFolderUrl(user.getId(), folderUrl);
        } else {
            folderUrl = "";
            files = fileStorageService.findByUserId(user.getId());
        }

        return new FolderDataDto(folderUrl, files);
    }

    @PostMapping(path = "/upload/file", consumes = {MediaType.MULTIPART_FORM_DATA_VALUE})
    @Operation(summary = "Загрузить файл")
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Краткая информация о загруженном файле",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = FilePresentDto.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Невалидный запрос",
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(value = "{\"message\": \"Файл не может быть пустым\"}")
                    )
            )
    })
    public FilePresentDto uploadFile(
            @RequestPart(value = "file") @NotNull MultipartFile file,
            @RequestParam(required = false) @Size(max = 255) String folderUrl,
            @AuthenticationPrincipal User user) {

        if (file.isEmpty()) {
            throw new FileInRequestIsEmptyException();
        }

        return new FilePresentDto(fileStorageService.uploadFile(user.getId(), file, folderUrl));
    }


    @Validate
    @PostMapping(path = "/upload/folder", consumes = {MediaType.MULTIPART_FORM_DATA_VALUE})
    @Operation(summary = "Загрузить папку целиком")
    @ApiResponse(
            responseCode = "200",
            description = "Список файлов этой папки с краткой информацией",
            content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = FilePresentDto.class)
            )
    )
    public Set<FilePresentDto> uploadFolder(
            @RequestPart(value = "files") @NotNull MultipartFile[] files,
            @RequestParam String folderName,
            @RequestParam(required = false, value = "paths") String[] paths,
            @RequestParam(required = false) String folderUrl,
            @AuthenticationPrincipal User user) {

        if (paths == null) {
            paths = new String[]{};
        }

        return GeneralMapper.toFilePresentDto(
                fileStorageService.uploadFolder(user.getId(), files, folderName, paths, folderUrl)
        );
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

    @GetMapping(path = "/download/folder")
    @Operation(summary = "Скачать папку архивом")
    @ApiResponse(
            responseCode = "200",
            description = "Папка в виде архива скачана"
    )
    public ResponseEntity<InputStreamResource> downloadFolder(@RequestParam String folderUrl, @AuthenticationPrincipal User user) {
        FileInputStreamWithAccessDto dto = fileStorageService.downloadFolder(user.getId(), folderUrl);
        String folderName = folderUrl.substring(folderUrl.lastIndexOf("/") + 1) + ".zip";
        return fileStorageService.getResourceForDownloadFolderByInputStream(dto, folderName);
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

    @PostMapping(path = "/move/folder")
    @Operation(summary = "Переместить папку")
    @ApiResponse(
            responseCode = "200",
            description = "Список файлов с краткой информацией",
            content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = FilePresentDto.class)
            )
    )
    public Set<FilePresentDto> moveFolder(@RequestParam String newFolderUrl, @RequestParam String oldFolderUrl, @AuthenticationPrincipal User user) {
        return GeneralMapper.toFilePresentDto(
                fileStorageService.moveFolderOnNewUrl(user.getId(), oldFolderUrl, newFolderUrl)
        );
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

    @PostMapping(path = "/rename/folder")
    @Operation(summary = "Переименовать папку")
    @ApiResponse(
            responseCode = "200",
            description = "Список файлов с краткой информацией",
            content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = FilePresentDto.class)
            )
    )
    public Set<FilePresentDto> renameFolder(@RequestParam String folderUrl, @RequestParam String newFolderName, @AuthenticationPrincipal User user) {
        return GeneralMapper.toFilePresentDto(
                fileStorageService.renameFolder(user.getId(), folderUrl, newFolderName)
        );
    }
}
