package org.y9nba.app.service;

import org.y9nba.app.dto.file.FIleCreateDto;
import org.y9nba.app.dto.file.FileDto;
import org.y9nba.app.dto.user.UserDto;
import org.y9nba.app.model.FileModel;

import java.util.Set;

public interface FileService {
    FileModel save(FIleCreateDto entity);
    void delete(FileModel entity);
    void deleteById(Long id);
    FileModel findById(Long id);
    boolean existsById(Long id);
    Set<FileDto> findByUser(Long userId);
}
