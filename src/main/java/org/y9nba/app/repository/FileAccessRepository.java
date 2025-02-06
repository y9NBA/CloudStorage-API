package org.y9nba.app.repository;

import org.springframework.stereotype.Repository;
import org.y9nba.app.base.repository.BaseRepository;
import org.y9nba.app.model.FileAccessModel;

import java.util.Set;

@Repository
public interface FileAccessRepository extends BaseRepository<FileAccessModel, Long> {

    Set<FileAccessModel> getFileAccessModelsByUserId(Long userId);
}
