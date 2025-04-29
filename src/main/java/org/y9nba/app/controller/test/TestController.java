package org.y9nba.app.controller.test;

import io.minio.messages.Bucket;
import io.swagger.v3.oas.annotations.Hidden;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.y9nba.app.dto.response.Response;
import org.y9nba.app.model.FileModel;
import org.y9nba.app.service.impl.*;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.logging.Logger;

@Hidden
@RestController
@RequestMapping("/test")
public class TestController {
    private final Logger logger = Logger.getLogger(TestController.class.getName());

    private final StorageServiceImpl storageService;

    public TestController(StorageServiceImpl storageService) {
        this.storageService = storageService;
    }

    @PostMapping(path = "/upload", consumes = {MediaType.MULTIPART_FORM_DATA_VALUE})
    public Response uploadFile(@RequestPart(value = "file", required = false) MultipartFile file) throws IOException {

        return new Response("OK");
    }

//    @GetMapping(path = "/download")
//    public void downloadFile(HttpServletResponse response) {
//        storageService.downloadFile("Test.txt", "y9nba", response);
//    }

    @GetMapping(path = "/download/{fileName}")
    public InputStream downloadFile(@PathVariable String fileName, HttpServletResponse response) throws IOException {
        InputStream inputStream = storageService.downloadFile(fileName, "y9nba");

        response.setHeader(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + fileName + "\"");
        response.flushBuffer();

        return inputStream;
    }
}
