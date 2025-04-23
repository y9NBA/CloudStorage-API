package org.y9nba.app.service;

import org.springframework.web.multipart.MultipartFile;
import org.y9nba.app.model.FileModel;

import java.io.InputStream;
import java.util.List;

public interface StorageService {
    void uploadFile(MultipartFile file, String bucketName);
    InputStream downloadFile(FileModel fileModel, String bucketName);
    InputStream downloadFile(String fileName, String bucketName);
    void downloadManyFiles(List<FileModel> fileModels, String bucketName);
}
