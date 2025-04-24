package org.y9nba.app.controller.file;

import jakarta.servlet.http.HttpServletResponse;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.y9nba.app.dto.file.FileDto;
import org.y9nba.app.model.FileModel;
import org.y9nba.app.model.UserModel;
import org.y9nba.app.service.impl.FileStorageServiceImpl;
import org.y9nba.app.service.impl.StorageServiceImpl;

import java.io.InputStream;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.List;

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

    @PostMapping(path = "/upload", consumes = {MediaType.MULTIPART_FORM_DATA_VALUE})
    public FileDto uploadFile(@RequestPart(value = "file", required = false) MultipartFile file, @RequestParam(required = false) String folderUrl, @AuthenticationPrincipal UserModel userModel) {
        FileModel fileModel = fileStorageService.uploadFile(userModel.getId(), file, folderUrl);
        return new FileDto(fileModel);
    }

    @GetMapping(path = "/download/{fileName}")
    public ResponseEntity<InputStreamResource> downloadFile(@PathVariable String fileName, @AuthenticationPrincipal UserModel userModel, HttpServletResponse response) {
        InputStream inputStream = fileStorageService.downloadFile(userModel.getId(), fileName);   // TODO: сделать проверку доступа к файлу или вынести данную логику в отдельный контроллер
        InputStreamResource resource = new InputStreamResource(inputStream);
        String encodedFileName = URLEncoder.encode(fileName, StandardCharsets.UTF_8).replaceAll("\\+", "%20");

        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + encodedFileName + "\"");
        headers.add(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_OCTET_STREAM_VALUE);

        return ResponseEntity.ok().headers(headers).body(resource);
    }
}
