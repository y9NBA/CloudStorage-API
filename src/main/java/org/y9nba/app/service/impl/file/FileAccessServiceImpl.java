package org.y9nba.app.service.impl.file;

import org.hibernate.Hibernate;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.y9nba.app.constant.Access;
import org.y9nba.app.dto.fileaccess.FileAccessCreateDto;
import org.y9nba.app.dto.fileaccess.FileAccessUpdateDto;
import org.y9nba.app.exception.web.file.access.NotFoundFileAccessByUserAndFileException;
import org.y9nba.app.dao.entity.FileAccess;
import org.y9nba.app.dao.repository.FileAccessRepository;
import org.y9nba.app.service.face.file.FileAccessService;

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

    @Cacheable(value = "FileAccessService::hasAccess", key = "#userId + '-' + #fileId + '-' + #accessLevel")
    @Override
    public boolean hasAccess(Long userId, Long fileId, Access accessLevel) {
        FileAccess faModel = findByUserAndFile(userId, fileId);
        return faModel.getAccessLevel().equals(accessLevel);
    }

    @Cacheable(value = "FileAccessService::hasAccessOnRead", key = "#userId + '-' + #fileId")
    @Override
    public boolean hasAccessOnRead(Long userId, Long fileId) {
        return hasAccess(userId, fileId, Access.ACCESS_READER) || hasAccess(userId, fileId, Access.ACCESS_EDITOR);
    }

    @Cacheable(value = "FileAccessService::hasAccessOnEdit", key = "#userId + '-' + #fileId")
    @Override
    public boolean hasAccessOnEdit(Long userId, Long fileId) {
        return hasAccess(userId, fileId, Access.ACCESS_EDITOR);
    }

    @Cacheable(value = "FileAccessService::findByUserAndFile", key = "#userId + '-' + #fileId")
    @Override
    public FileAccess findByUserAndFile(Long userId, Long fileId) {
        return repository
                .findByUserIdAndFileId(userId, fileId)
                .orElseThrow(
                        () -> new NotFoundFileAccessByUserAndFileException(fileId, userId)
                );
    }

    @CacheEvict(value = {
            "FileAccessService::hasAccess",
            "FileAccessService::findByUserAndFile",
            "FileAccessService::findByUser",
            "FileAccessService::hasAccessOnRead",
            "FileAccessService::hasAccessOnEdit"
    })
    @Override
    public void deleteAllAccessesForFile(Long fileId) {
        deleteAll(repository.getFileAccessesByFileId(fileId));
    }

    @CacheEvict(value = {
            "FileAccessService::hasAccess",
            "FileAccessService::findByUserAndFile",
            "FileAccessService::findByUser",
            "FileAccessService::hasAccessOnRead",
            "FileAccessService::hasAccessOnEdit"
    })
    @Override
    public void deleteAllAccessesReaderForFile(Long fileId) {
        deleteAll(
                repository
                        .getFileAccessesByFileId(fileId)
                        .stream()
                        .filter(
                                fa -> fa.getAccessLevel().equals(Access.ACCESS_READER)
                        ).collect(Collectors.toSet())
        );
    }

    private void deleteAll(Set<FileAccess> fileAccesses) {
        repository.deleteAll(fileAccesses);
    }

    @CacheEvict(value = {
            "FileAccessService::hasAccess",
            "FileAccessService::findByUserAndFile",
            "FileAccessService::findByUser",
            "FileAccessService::hasAccessOnRead",
            "FileAccessService::hasAccessOnEdit"
    })
    @Override
    public void delete(FileAccess entity) {
        repository.delete(entity);
    }

    @Override
    public void deleteByUserIdAndFileId(Long userId, Long fileId) {
        delete(findByUserAndFile(userId, fileId));
    }

    @Override
    public boolean existsByUserAndFile(Long userId, Long fileId) {
        return repository.existsByUserIdAndFileId(userId, fileId);
    }

    @Cacheable(value = "FileAccessService::findByUser", key = "#userId")
    @Transactional
    @Override
    public Set<FileAccess> findByUser(Long userId) {
        Set<FileAccess> fileAccesses = repository.getFileAccessesByUserId(userId);
        fileAccesses.forEach(fa -> Hibernate.initialize(fa.getFile()));
        return fileAccesses;
    }
}
