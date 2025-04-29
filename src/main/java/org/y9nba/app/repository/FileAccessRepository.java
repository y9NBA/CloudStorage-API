package org.y9nba.app.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.y9nba.app.model.FileAccessModel;

import java.util.Optional;
import java.util.Set;

@Repository
public interface FileAccessRepository extends JpaRepository<FileAccessModel, Long> {

    Optional<FileAccessModel> findByUserIdAndFileId(Long userId, Long fileId);
    Set<FileAccessModel> getFileAccessModelsByUserId(Long userId);
}
