package org.y9nba.app.service;

import org.springframework.web.multipart.MultipartFile;
import org.y9nba.app.dto.file.FileCreateDto;
import org.y9nba.app.dto.file.FileUpdateDto;
import org.y9nba.app.exception.local.NotPhysicalFileException;
import org.y9nba.app.exception.local.PhysicalFileOnUrlAlreadyException;
import org.y9nba.app.exception.local.PhysicalFilesAndEntriesNotSyncException;
import org.y9nba.app.model.FileModel;
import org.y9nba.app.model.UserModel;

import java.io.InputStream;
import java.util.List;
import java.util.Set;

public interface StorageService {
    void uploadFile(MultipartFile file, String bucketName, String folderUrl);
    InputStream downloadFile(String bucketName, FileModel fileModel) throws NotPhysicalFileException;
    void downloadManyFiles(String bucketName, List<FileModel> fileModels);
    void deleteFile(String bucketName, FileModel fileModel);
    void moveFile(String bucketName, FileModel fileModel, FileUpdateDto fileUpdDto) throws NotPhysicalFileException, PhysicalFileOnUrlAlreadyException;
    void copyFile(String bucketName, FileModel fileModel, FileCreateDto fileCrtDto) throws NotPhysicalFileException, PhysicalFileOnUrlAlreadyException;
    void synchronizeFile(String bucketName, Set<FileModel> fileModels, UserModel user) throws PhysicalFilesAndEntriesNotSyncException;
}
