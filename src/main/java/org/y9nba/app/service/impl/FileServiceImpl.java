package org.y9nba.app.service.impl;

import org.springframework.stereotype.Service;
import org.y9nba.app.dto.file.FIleCreateDto;
import org.y9nba.app.dto.file.FileDto;
import org.y9nba.app.dto.user.UserDto;
import org.y9nba.app.repository.FileRepository;
import org.y9nba.app.service.FileService;

import java.util.Set;

@Service
public class FileServiceImpl implements FileService {

    private final FileRepository repository;

    public FileServiceImpl(FileRepository repository) {
        this.repository = repository;
    }

    @Override
    public FileDto save(FIleCreateDto entity) {
        return null;
    }

    @Override
    public void delete(FileDto entity) {

    }

    @Override
    public void deleteById(Long id) {

    }

    @Override
    public FileDto findById(Long id) {
        return new FileDto();
    }

    @Override
    public boolean existsById(Long id) {
        return false;
    }

    @Override
    public Set<FileDto> findByUser(Long userId) {
        return null;
    }
}
