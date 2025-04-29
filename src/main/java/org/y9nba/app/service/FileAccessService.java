package org.y9nba.app.service;

import org.y9nba.app.constant.Access;
import org.y9nba.app.dto.fileaccess.FileAccessCreateDto;
import org.y9nba.app.model.FileAccessModel;

import java.util.Set;

public interface FileAccessService {
    FileAccessModel save(FileAccessCreateDto entity);
    boolean hasAccess(Long userId, Long fileId, Access accessLevel);
    boolean hasAccessOnRead(Long userId, Long fileId);
    boolean hasAccessOnEdit(Long userId, Long fileId);
    FileAccessModel findByUserAndFile(Long userId, Long fileId);
    void delete(FileAccessModel entity);
    void deleteById(Long id);
    FileAccessModel findById(Long id);
    boolean existsById(Long id);
    Set<FileAccessModel> findByUser(Long userId);
}
