package org.y9nba.app.service.impl;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpStatusCodeException;
import org.y9nba.app.dto.fileaccess.FileAccessCreateDto;
import org.y9nba.app.dto.fileaccess.FileAccessDto;
import org.y9nba.app.dto.user.UserDto;
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

    @Override
    public Set<FileAccessDto> findByUser(Long userId) {
        return null;
    }
}
