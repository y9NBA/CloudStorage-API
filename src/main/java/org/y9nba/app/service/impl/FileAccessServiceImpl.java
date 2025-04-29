package org.y9nba.app.service.impl;

import org.hibernate.Hibernate;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.HttpClientErrorException;
import org.y9nba.app.constant.Access;
import org.y9nba.app.dto.fileaccess.FileAccessCreateDto;
import org.y9nba.app.exception.web.NotFoundEntryException;
import org.y9nba.app.model.FileAccessModel;
import org.y9nba.app.repository.FileAccessRepository;
import org.y9nba.app.service.FileAccessService;

import java.util.Set;

@Service
public class FileAccessServiceImpl implements FileAccessService {

    private final FileAccessRepository repository;

    public FileAccessServiceImpl(FileAccessRepository repository) {
        this.repository = repository;
    }

    @Override
    public FileAccessModel save(FileAccessCreateDto entity) {
        return repository.save(new FileAccessModel(entity));
    }

    @Override
    public boolean hasAccess(Long userId, Long fileId, Access accessLevel) {
        FileAccessModel faModel = findByUserAndFile(userId, fileId);
        return faModel.getAccessLevel().equals(accessLevel);
    }

    @Override
    public boolean hasAccessOnRead(Long userId, Long fileId) {
        FileAccessModel faModel = findByUserAndFile(userId, fileId);
        return faModel.getAccessLevel().equals(Access.ACCESS_READER) || faModel.getAccessLevel().equals(Access.ACCESS_EDITOR);
    }

    @Override
    public boolean hasAccessOnEdit(Long userId, Long fileId) {
        FileAccessModel faModel = findByUserAndFile(userId, fileId);
        return faModel.getAccessLevel().equals(Access.ACCESS_EDITOR);
    }

    @Override
    public FileAccessModel findByUserAndFile(Long userId, Long fileId) {
        return repository
                .findByUserIdAndFileId(userId, fileId)
                .orElseThrow(
                        () -> new NotFoundEntryException("Not found file access.\nFileId: " + fileId + "\nUserId: " + userId)
                );
    }

    @Override
    public void delete(FileAccessModel entity) {

    }

    @Override
    public void deleteById(Long id) {

    }

    @Override
    public FileAccessModel findById(Long id) {
        return repository
                .findById(id)
                .orElseThrow(
                        () -> new HttpClientErrorException(HttpStatus.BAD_REQUEST)
                );
    }

    @Override
    public boolean existsById(Long id) {
        return false;
    }

    @Transactional
    @Override
    public Set<FileAccessModel> findByUser(Long userId) {
        Set<FileAccessModel> fileAccessModels = repository.getFileAccessModelsByUserId(userId);
        fileAccessModels.forEach(fa -> Hibernate.initialize(fa.getFile()));
        return fileAccessModels;
    }
}
