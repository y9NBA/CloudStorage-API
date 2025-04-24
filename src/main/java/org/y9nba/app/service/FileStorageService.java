package org.y9nba.app.service;

import org.springframework.web.multipart.MultipartFile;
import org.y9nba.app.dto.file.FileCreateDto;
import org.y9nba.app.dto.file.FileUpdateDto;
import org.y9nba.app.model.FileModel;

import java.io.InputStream;
import java.util.Set;

public interface FileStorageService {
    FileModel uploadFile(Long userId, MultipartFile file);
    FileModel uploadFile(Long userId, MultipartFile file, String folderURL);
    InputStream downloadFile(Long userId, String fileName);
    InputStream downloadFile(Long userId, String fileName, String folderURL);
    FileModel save(FileCreateDto dto);
    FileModel update(FileUpdateDto dto);
    void deleteFile(Long userId, String fileName);
    void deleteFile(Long userId, String fileName, String folderURL);
    void deleteById(Long id);
    FileModel findById(Long id);
    FileModel findByUserIdAndUrl(Long userId, String url);
    boolean existsByURL(String url);
    Set<FileModel> findByUserId(Long userId);
    Set<FileModel> findByUserIdAndFolderUrl(Long userId, String folderURL);
}
