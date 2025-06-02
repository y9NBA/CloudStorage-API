package org.y9nba.app.dao.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.y9nba.app.dao.entity.FileAccess;

import java.util.Optional;
import java.util.Set;

@Repository
public interface FileAccessRepository extends JpaRepository<FileAccess, Long> {

    Optional<FileAccess> findByUserIdAndFileId(Long userId, Long fileId);
    Set<FileAccess> getFileAccessesByUserId(Long userId);
    Set<FileAccess> getFileAccessesByFileId(Long fileId);
    boolean existsByUserIdAndFileId(Long userId, Long fileId);
}
