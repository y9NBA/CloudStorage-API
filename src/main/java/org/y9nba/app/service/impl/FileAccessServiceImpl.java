package org.y9nba.app.service.impl;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpStatusCodeException;
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
    public FileAccessDto save(FileAccessDto entity) {
        return null;
    }

    @Override
    public void delete(FileAccessDto entity) {

    }

    @Override
    public void deleteById(Long id) {

    }

    @Override
    public FileAccessDto findById(Long id) {
        FileAccessModel model =
                repository
                        .findById(id)
                        .orElseThrow(
                                () -> new HttpClientErrorException(HttpStatus.BAD_REQUEST)
                        );

        return new FileAccessDto(model);
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
