package org.y9nba.app.service;

import org.springframework.core.io.InputStreamResource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;
import org.y9nba.app.constant.Access;
import org.y9nba.app.dto.file.FileCreateDto;
import org.y9nba.app.dto.file.FileInputStreamWithAccessDto;
import org.y9nba.app.dto.file.FileUpdateDto;
import org.y9nba.app.model.FileModel;

import java.util.Set;

public interface FileStorageService {
    FileModel uploadFile(Long userId, MultipartFile file, String folderURL);
    FileModel uploadFileByAccess(Long userId, MultipartFile file, String bucketName, String fileName, String folderURL);
    FileInputStreamWithAccessDto downloadFile(Long userId, String fileName, String folderURL);
    FileInputStreamWithAccessDto downloadFileByAccess(Long userId, String bucketName, String fileName, String folderURL);
    FileModel moveFileOnNewUrl(Long userId, String fileName, String newFolderURL, String oldFolderURL);
    FileModel copyExistingFile(Long userId, String fileName, String folderURL);
    FileModel renameFile(Long userId, String fileName, String newFileName, String folderURL);
    FileModel save(FileCreateDto dto);
    FileModel update(FileUpdateDto dto);
    String deleteFile(Long userId, String fileName, String folderURL);
    FileModel giveAccessOnFileForUser(Long userId, String fileName, String folderURL, Long collaboratorUserId, Access access);
    FileModel revokeAccessOnFileForUser(Long userId, String fileName, String folderURL, Long collaboratorUserId);
    FileModel revokeAllAccessOnFile(Long userId, String fileName, String folderURL);
    FileModel makeFilePublic(Long userId, String fileName, String folderURL);
    FileModel makeFilePrivate(Long userId, String fileName, String folderURL);
    Set<FileModel> findByUserId(Long userId);
    Set<FileModel> findByUserIdAndFolderUrl(Long userId, String folderURL);
    FileModel findOwnerByFileId(Long userId, String bucketName, String fileName, String folderURL);
    Set<FileModel> findOwnerByUserId(Long userId);
    Set<FileModel> findOwnerByUserIdAndFolderUrl(Long userId, String folderURL);
    boolean existsByURL(String url);
    ResponseEntity<InputStreamResource> getResourceForViewByInputStream(FileInputStreamWithAccessDto dto, FileModel fileModel);
    ResponseEntity<InputStreamResource> getResourceForDownloadByInputStream(FileInputStreamWithAccessDto dto, FileModel fileModel);
    void refreshFiles(Long userId);
}
