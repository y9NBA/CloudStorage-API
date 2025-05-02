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
    InputStream downloadFile( String bucketName, String fileName);
    void downloadManyFiles(String bucketName, List<FileModel> fileModels);
    void deleteFile(String bucketName, FileModel fileModel);
    void deleteFile(String bucketName, String fileName);
    void moveFile(String bucketName, FileModel fileModel, FileUpdateDto fileUpdDto) throws NotPhysicalFileException, PhysicalFileOnUrlAlreadyException;
    void copyFile(String bucketName, FileModel fileModel, FileCreateDto fileCrtDto) throws NotPhysicalFileException, PhysicalFileOnUrlAlreadyException;
    boolean isFileExist(String bucketName, String fileName);
    boolean isFileExist(String bucketName, FileModel fileModel);
    String shareFile(String bucketName, FileModel fileModel, int minutes) throws NotPhysicalFileException;
    String shareFile(String bucketName, String fileName, int minutes);
    void synchronizeFile(String bucketName, Set<FileModel> fileModels, UserModel user) throws PhysicalFilesAndEntriesNotSyncException;
}
