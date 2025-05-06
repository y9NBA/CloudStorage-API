package org.y9nba.app.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.y9nba.app.model.FileModel;

import java.util.Optional;
import java.util.Set;

@Repository
public interface FileRepository extends JpaRepository<FileModel, Long> {

    Set<FileModel> getFileModelsByUser_Id(Long userId);
    Set<FileModel> getFileModelsByUser_IdAndUrlContaining(Long userId, String url);
    Optional<FileModel> getFileModelByUser_IdAndUrl(Long userId, String url);
    Optional<FileModel> getFileModelByUrl(String url);
    boolean existsByUrl(String url);
}
