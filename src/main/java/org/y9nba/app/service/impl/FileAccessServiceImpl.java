package org.y9nba.app.service.impl;

import org.springframework.stereotype.Service;
import org.y9nba.app.base.service.BaseServiceImpl;
import org.y9nba.app.model.FileAccessModel;
import org.y9nba.app.repository.FileAccessRepository;
import org.y9nba.app.service.FileAccessService;

import java.util.Set;

@Service
public class FileAccessServiceImpl extends BaseServiceImpl<FileAccessRepository, FileAccessModel, Long> implements FileAccessService {

    public FileAccessServiceImpl(FileAccessRepository repository) {
        super(repository);
    }

    @Override
    public Set<FileAccessModel> findByUser(Long userId) {
        return repository.getFileAccessModelsByUserId(userId);
    }
}
