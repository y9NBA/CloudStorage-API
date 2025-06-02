package org.y9nba.app.service.face.file;

import org.springframework.web.multipart.MultipartFile;
import org.y9nba.app.dto.file.FileCreateDto;
import org.y9nba.app.dto.file.FileUpdateDto;
import org.y9nba.app.exception.local.NotPhysicalFileException;
import org.y9nba.app.exception.local.PhysicalFileOnUrlAlreadyException;
import org.y9nba.app.exception.local.PhysicalFilesAndEntriesNotSyncException;
import org.y9nba.app.dao.entity.File;
import org.y9nba.app.dao.entity.User;

import java.io.InputStream;
import java.util.List;
import java.util.Set;

public interface StorageService {
    void uploadFile(MultipartFile file, String bucketName, String folderUrl);
    // void uploadFolder();
    InputStream downloadFile(String bucketName, File file) throws NotPhysicalFileException;
    void downloadFolder(String bucketName, List<File> files);
    void deleteFile(String bucketName, File file) throws NotPhysicalFileException;
    void deleteFiles(String bucketName, Set<File> files);
    void moveFile(String bucketName, File file, FileUpdateDto fileUpdDto) throws NotPhysicalFileException, PhysicalFileOnUrlAlreadyException;
    void copyFile(String bucketName, File file, FileCreateDto fileCrtDto) throws NotPhysicalFileException, PhysicalFileOnUrlAlreadyException;
    void synchronizeFile(String bucketName, Set<File> files, User user) throws PhysicalFilesAndEntriesNotSyncException;
    void deleteBucket(String bucketName);
}
