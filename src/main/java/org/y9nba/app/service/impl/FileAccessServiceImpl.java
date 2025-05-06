package org.y9nba.app.service.impl;

import org.hibernate.Hibernate;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.HttpClientErrorException;
import org.y9nba.app.constant.Access;
import org.y9nba.app.dto.fileaccess.FileAccessCreateDto;
import org.y9nba.app.dto.fileaccess.FileAccessUpdateDto;
import org.y9nba.app.exception.web.NotFoundEntryException;
import org.y9nba.app.model.FileAccessModel;
import org.y9nba.app.repository.FileAccessRepository;
import org.y9nba.app.service.FileAccessService;

import java.util.Set;
import java.util.stream.Collectors;

@Service
public class FileAccessServiceImpl implements FileAccessService {

    private final FileAccessRepository repository;

    public FileAccessServiceImpl(FileAccessRepository repository) {
        this.repository = repository;
    }

    @Override
    public FileAccessModel save(FileAccessCreateDto dto) {    // TODO: сделать методы на добавление и удаление доступа к файлу
        return repository.save(new FileAccessModel(dto));
    }

    @Override
    public FileAccessModel update(FileAccessUpdateDto dto) {
        return repository.save(new FileAccessModel(dto));
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
                        () -> new NotFoundEntryException("Not found file access. FileId: " + fileId + "; UserId: " + userId)
                );
    }

    @Override
    public void deleteAllAccessesForFile(Long fileId) {
        deleteAll(repository.getFileAccessModelsByFileId(fileId));
    }

    @Override
    public void deleteAllAccessesReaderForFile(Long fileId) {
        deleteAll(
                repository
                        .getFileAccessModelsByFileId(fileId)
                        .stream()
                        .filter(
                                fa -> fa.getAccessLevel().equals(Access.ACCESS_READER)
                        ).collect(Collectors.toSet())
        );
    }

    private void deleteAll(Set<FileAccessModel> fileAccessModels) {
        repository.deleteAll(fileAccessModels);
    }

    @Override
    public void delete(FileAccessModel entity) {
        repository.delete(entity);
    }

    @Override
    public void deleteById(Long id) {
        repository.deleteById(id);
    }

    @Override
    public void deleteByUserIdAndFileId(Long userId, Long fileId) {
        delete(findByUserAndFile(userId, fileId));
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
    public boolean existsByUserAndFile(Long userId, Long fileId) {
        return repository.existsByUserIdAndFileId(userId, fileId);
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
