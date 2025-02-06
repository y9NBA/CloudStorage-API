package org.y9nba.app.repository;

import org.springframework.stereotype.Repository;
import org.y9nba.app.base.repository.BaseRepository;
import org.y9nba.app.model.FileModel;

import java.util.Set;

@Repository
public interface FileRepository extends BaseRepository<FileModel, Long> {

    Set<FileModel> getFileModelsByUserId(Long userId);
}
