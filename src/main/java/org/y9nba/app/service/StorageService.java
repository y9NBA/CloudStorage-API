package org.y9nba.app.service;

import org.springframework.web.multipart.MultipartFile;
import org.y9nba.app.model.FileModel;

import java.io.InputStream;
import java.util.List;

public interface StorageService {
    void uploadFile(MultipartFile file, String bucketName);
    void uploadFile(MultipartFile file, String bucketName, String folderUrl);
    InputStream downloadFile(String bucketName, FileModel fileModel);
    InputStream downloadFile( String bucketName, String fileName);
    InputStream downloadFileByUrl(String bucketName, String fileUrl);
    void downloadManyFiles(String bucketName, List<FileModel> fileModels);
}
