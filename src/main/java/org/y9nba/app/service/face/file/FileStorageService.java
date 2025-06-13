package org.y9nba.app.service.face.file;

import org.springframework.core.io.InputStreamResource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;
import org.y9nba.app.constant.Access;
import org.y9nba.app.dto.file.FileCreateDto;
import org.y9nba.app.dto.file.FileInputStreamWithAccessDto;
import org.y9nba.app.dto.file.FileUpdateDto;
import org.y9nba.app.dao.entity.File;

import java.util.Set;

public interface FileStorageService {
    File uploadFile(Long userId, MultipartFile file, String folderURL);
    Set<File> uploadFolder(Long userId, MultipartFile[] files, String folderName, String[] paths, String folderURL);
    File uploadFileByAccess(Long userId, MultipartFile file, String bucketName, String fileName, String folderURL);
    FileInputStreamWithAccessDto downloadFile(Long userId, String fileName, String folderURL);
    FileInputStreamWithAccessDto downloadFolder(Long userId, String folderURL);
    FileInputStreamWithAccessDto downloadFileByAccess(Long userId, String bucketName, String fileName, String folderURL);
    File moveFileOnNewUrl(Long userId, String fileName, String newFolderURL, String oldFolderURL);
    Set<File> moveFolderOnNewUrl(Long userId, String oldFolderURL, String newFolderURL);
    File copyExistingFile(Long userId, String fileName, String folderURL);
    File renameFile(Long userId, String fileName, String newFileName, String folderURL);
    Set<File> renameFolder(Long userId, String folderURL, String newFolderName);
    File save(FileCreateDto dto);
    File update(FileUpdateDto dto);
    String deleteFile(Long userId, String fileName, String folderURL);
    String deleteFilesByFolder(Long userId, String folderURL);
    void deleteAllFilesByDeletedUserId(Long userId);
    File giveAccessOnFileForUser(Long userId, String fileName, String folderURL, Long collaboratorUserId, Access access);
    File revokeAccessOnFileForUser(Long userId, String fileName, String folderURL, Long collaboratorUserId);
    File revokeAllAccessOnFile(Long userId, String fileName, String folderURL);
    File makeFilePublic(Long userId, String fileName, String folderURL);
    File makeFilePrivate(Long userId, String fileName, String folderURL);
    Set<File> findByUserId(Long userId);
    Set<File> findByUserIdAndFolderUrl(Long userId, String folderURL);
    File findOwnerByBucketNameAndFileNameAndFolderUrl(Long userId, String bucketName, String fileName, String folderURL);
    Set<File> findOwnerByUserId(Long userId);
    Set<File> findOwnerByUserIdAndFolderUrl(Long userId, String folderURL);
    boolean existsByURL(String url);
    ResponseEntity<InputStreamResource> getResourceForViewByInputStream(FileInputStreamWithAccessDto dto, File file);
    ResponseEntity<InputStreamResource> getResourceForDownloadByInputStream(FileInputStreamWithAccessDto dto, File file);
    ResponseEntity<InputStreamResource> getResourceForDownloadFolderByInputStream(FileInputStreamWithAccessDto dto, String folderName);
    void refreshFiles(Long userId);
}
