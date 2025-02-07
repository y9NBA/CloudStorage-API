package org.y9nba.app.service;

import org.y9nba.app.dto.fileaccess.FileAccessDto;
import org.y9nba.app.dto.user.UserDto;

import java.util.Set;

public interface FileAccessService {
    FileAccessDto save(FileAccessDto entity);
    void delete(FileAccessDto entity);
    void deleteById(Long id);
    FileAccessDto findById(Long id);
    boolean existsById(Long id);
    Set<FileAccessDto> findByUser(Long userId);
}
