package org.y9nba.app.dao.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.y9nba.app.dao.entity.File;

import java.util.Optional;
import java.util.Set;

@Repository
public interface FileRepository extends JpaRepository<File, Long> {

    Set<File> getFilesByUser_Id(Long userId);
    Set<File> getFilesByUser_IdAndUrlContaining(Long userId, String url);
    Set<File> getFilesByIsPublicTrue();
    Set<File> getFilesByUser_IdAndIsPublicTrue(Long userId);
    Optional<File> getFileByIdAndIsPublicTrue(Long id);
    Optional<File> getFileByUser_IdAndUrl(Long userId, String url);
    Optional<File> getFileByUrl(String url);
    boolean existsByUrl(String url);
}
