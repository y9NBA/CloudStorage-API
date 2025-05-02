package org.y9nba.app.service;

import org.springframework.core.io.InputStreamResource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;
import org.y9nba.app.dto.file.FileCreateDto;
import org.y9nba.app.dto.file.FileUpdateDto;
import org.y9nba.app.dto.share.ExpireRequestDto;
import org.y9nba.app.dto.share.SharedUrlResponseDto;
import org.y9nba.app.model.FileModel;

import java.io.InputStream;
import java.util.Set;

public interface FileStorageService {
    FileModel uploadFile(Long userId, MultipartFile file, String folderURL);
    InputStream downloadFile(Long userId, String fileName, String folderURL);
    InputStream downloadFileByAccess(Long userId, Long fileId);
    FileModel moveFileOnNewUrl(Long userId, String fileName, String newFolderURL, String oldFolderURL);
    FileModel save(FileCreateDto dto);
    FileModel update(FileUpdateDto dto);
    String deleteFile(Long userId, String fileName, String folderURL);
    FileModel findById(Long id);
    FileModel findByUserIdAndUrl(Long userId, String url);
    FileModel findFile(Long userId, String fileName, String folderURL);
    Set<FileModel> findByUserId(Long userId);
    Set<FileModel> findByUserIdAndFolderUrl(Long userId, String folderURL);
    FileModel findOwnerByFileId(Long userId, Long fileId);
    Set<FileModel> findOwnerByUserId(Long userId);
    Set<FileModel> findOwnerByUserIdAndFolderUrl(Long userId, String folderURL);
    boolean existsByURL(String url);
    SharedUrlResponseDto getSharedUrlForFile(ExpireRequestDto expireRequestDto, Long userId, String fileName, String folderURL);
    ResponseEntity<InputStreamResource> getResourceByInputStream(InputStream inputStream, String fileName);
    void refreshFiles(Long userId);
}
