package org.y9nba.app.service.face.file;

import org.y9nba.app.constant.Access;
import org.y9nba.app.dto.fileaccess.FileAccessCreateDto;
import org.y9nba.app.dto.fileaccess.FileAccessUpdateDto;
import org.y9nba.app.dao.entity.FileAccess;

import java.util.Set;

public interface FileAccessService {
    FileAccess save(FileAccessCreateDto dto);
    FileAccess update(FileAccessUpdateDto dto);
    boolean hasAccess(Long userId, Long fileId, Access accessLevel);
    boolean hasAccessOnRead(Long userId, Long fileId);
    boolean hasAccessOnEdit(Long userId, Long fileId);
    FileAccess findByUserAndFile(Long userId, Long fileId);
    void deleteAllAccessesForFile(Long fileId);
    void deleteAllAccessesReaderForFile(Long fileId);
    void delete(FileAccess entity);
    void deleteById(Long id);
    void deleteByUserIdAndFileId(Long userId, Long fileId);
    FileAccess findById(Long id);
    boolean existsByUserAndFile(Long userId, Long fileId);
    boolean existsById(Long id);
    Set<FileAccess> findByUser(Long userId);
}
