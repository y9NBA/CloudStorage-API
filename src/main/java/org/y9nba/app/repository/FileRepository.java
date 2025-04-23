package org.y9nba.app.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.y9nba.app.model.FileModel;

import java.util.Optional;
import java.util.Set;

@Repository
public interface FileRepository extends JpaRepository<FileModel, Long> {

    Set<FileModel> getFileModelsByUser_Username(String username);
    Set<FileModel> getFileModelsByUser_UsernameAndUrlContaining(String username, String url);
    Optional<FileModel> getFileModelByUser_UsernameAndUrl(String username, String url);
    boolean existsByUrl(String url);
}
