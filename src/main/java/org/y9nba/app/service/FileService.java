package org.y9nba.app.service;

import org.y9nba.app.dto.file.FIleCreateDto;
import org.y9nba.app.dto.file.FileDto;
import org.y9nba.app.dto.user.UserDto;

import java.util.Set;

public interface FileService {
    FileDto save(FIleCreateDto entity);
    void delete(FileDto entity);
    void deleteById(Long id);
    FileDto findById(Long id);
    boolean existsById(Long id);
    Set<FileDto> findByUser(Long userId);
}
