package org.y9nba.app.service;

import org.springframework.web.multipart.MultipartFile;
import org.y9nba.app.dto.file.FileCreateDto;
import org.y9nba.app.dto.file.FileUpdateDto;
import org.y9nba.app.model.FileModel;

import java.io.InputStream;
import java.util.Set;

public interface FileStorageService {
    FileModel uploadFile(String username, MultipartFile file);
    FileModel uploadFile(String username, MultipartFile file, String folderURL);
    InputStream downloadFile(String username, String fileName);
    InputStream downloadFile(String username, String fileName, String folderURL);
    FileModel save(FileCreateDto dto);
    FileModel update(FileUpdateDto dto);
    void deleteFile(String username, String fileName);
    void deleteFile(String username, String fileName, String folderURL);
    void deleteById(Long id);
    FileModel findById(Long id);
    FileModel findByUsernameAndUrl(String username, String url);
    boolean existsByURL(String url);
    Set<FileModel> findByUsername(String username);
    Set<FileModel> findByUsernameAndFolderUrl(String username, String folderURL);
}
