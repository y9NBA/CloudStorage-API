package org.y9nba.app.service;

import org.y9nba.app.dto.fileaccess.FileAccessCreateDto;
import org.y9nba.app.dto.fileaccess.FileAccessDto;
import org.y9nba.app.dto.user.UserDto;
import org.y9nba.app.model.FileAccessModel;

import java.util.Set;

public interface FileAccessService {
    FileAccessModel save(FileAccessCreateDto entity);
    void delete(FileAccessModel entity);
    void deleteById(Long id);
    FileAccessModel findById(Long id);
    boolean existsById(Long id);
    Set<FileAccessDto> findByUser(Long userId);
}
