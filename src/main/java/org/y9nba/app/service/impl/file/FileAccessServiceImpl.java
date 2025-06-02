package org.y9nba.app.service.impl;

import org.hibernate.Hibernate;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.HttpClientErrorException;
import org.y9nba.app.constant.Access;
import org.y9nba.app.dto.fileaccess.FileAccessCreateDto;
import org.y9nba.app.dto.fileaccess.FileAccessUpdateDto;
import org.y9nba.app.exception.web.file.access.NotFoundFileAccessByUserAndFileException;
import org.y9nba.app.dao.entity.FileAccess;
import org.y9nba.app.dao.repository.FileAccessRepository;
import org.y9nba.app.service.face.FileAccessService;

import java.util.Set;
import java.util.stream.Collectors;

@Service
public class FileAccessServiceImpl implements FileAccessService {

    private final FileAccessRepository repository;

    public FileAccessServiceImpl(FileAccessRepository repository) {
        this.repository = repository;
    }

    @Override
    public FileAccess save(FileAccessCreateDto dto) {
        return repository.save(new FileAccess(dto));
    }

    @Override
    public FileAccess update(FileAccessUpdateDto dto) {
        return repository.save(new FileAccess(dto));
    }

    @Override
    public boolean hasAccess(Long userId, Long fileId, Access accessLevel) {
        FileAccess faModel = findByUserAndFile(userId, fileId);
        return faModel.getAccessLevel().equals(accessLevel);
    }

    @Override
    public boolean hasAccessOnRead(Long userId, Long fileId) {
        FileAccess faModel = findByUserAndFile(userId, fileId);
        return faModel.getAccessLevel().equals(Access.ACCESS_READER) || faModel.getAccessLevel().equals(Access.ACCESS_EDITOR);
    }

    @Override
    public boolean hasAccessOnEdit(Long userId, Long fileId) {
        FileAccess faModel = findByUserAndFile(userId, fileId);
        return faModel.getAccessLevel().equals(Access.ACCESS_EDITOR);
    }

    @Override
    public FileAccess findByUserAndFile(Long userId, Long fileId) {
        return repository
                .findByUserIdAndFileId(userId, fileId)
                .orElseThrow(
                        () -> new NotFoundFileAccessByUserAndFileException(fileId, userId)
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

    private void deleteAll(Set<FileAccess> fileAccesses) {
        repository.deleteAll(fileAccesses);
    }

    @Override
    public void delete(FileAccess entity) {
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
    public FileAccess findById(Long id) {
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
    public Set<FileAccess> findByUser(Long userId) {
        Set<FileAccess> fileAccesses = repository.getFileAccessModelsByUserId(userId);
        fileAccesses.forEach(fa -> Hibernate.initialize(fa.getFile()));
        return fileAccesses;
    }
}
