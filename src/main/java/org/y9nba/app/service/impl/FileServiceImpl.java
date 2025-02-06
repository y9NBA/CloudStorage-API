package org.y9nba.app.service.impl;

import org.springframework.stereotype.Service;
import org.y9nba.app.base.service.BaseServiceImpl;
import org.y9nba.app.model.FileModel;
import org.y9nba.app.repository.FileRepository;
import org.y9nba.app.service.FileService;

import java.util.Set;

@Service
public class FileServiceImpl extends BaseServiceImpl<FileRepository, FileModel, Long> implements FileService {

    public FileServiceImpl(FileRepository repository) {
        super(repository);
    }

    @Override
    public Set<FileModel> findByUser(Long userId) {
        return repository.getFileModelsByUserId(userId);
    }
}
