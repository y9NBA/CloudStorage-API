package org.y9nba.app.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.y9nba.app.model.FileModel;

import java.util.Set;

@Repository
public interface FileRepository extends JpaRepository<FileModel, Long> {

    Set<FileModel> getFileModelsByUserId(Long userId);
}
