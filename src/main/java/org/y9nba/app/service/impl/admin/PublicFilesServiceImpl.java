package org.y9nba.app.service.impl.admin;

import org.springframework.core.io.InputStreamResource;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.y9nba.app.dao.entity.File;
import org.y9nba.app.dao.entity.User;
import org.y9nba.app.dao.repository.FileRepository;
import org.y9nba.app.dto.file.FileInputStreamWithAccessDto;
import org.y9nba.app.dto.file.FilePresentDto;
import org.y9nba.app.exception.web.file.search.NotFoundPublicFileByIdException;
import org.y9nba.app.service.face.admin.PublicFilesService;
import org.y9nba.app.service.impl.file.FileStorageServiceImpl;
import org.y9nba.app.service.impl.user.UserServiceImpl;

import java.util.Set;

@Service
public class PublicFilesServiceImpl implements PublicFilesService {

    private final UserServiceImpl userService;
    private final FileRepository fileRepository;
    private final FileStorageServiceImpl fileStorageService;

    public PublicFilesServiceImpl(UserServiceImpl userService, FileRepository fileRepository, FileStorageServiceImpl fileStorageService) {
        this.userService = userService;
        this.fileRepository = fileRepository;
        this.fileStorageService = fileStorageService;
    }

    @Override
    public Set<File> getPublicFilesByUserId(Long userId) {
        userService.getById(userId);
        return fileRepository.getFilesByUser_IdAndIsPublicTrue(userId);
    }

    @Override
    public Set<File> getAllPublicFiles() {
        return fileRepository.getFilesByIsPublicTrue();
    }

    @Override
    public ResponseEntity<InputStreamResource> viewPublicFile(Long fileId) {
        File file = getPublicFileById(fileId);
        FilePresentDto dto = new FilePresentDto(file);
        FileInputStreamWithAccessDto fileInputStreamWithAccessDto = fileStorageService
                .downloadFileByAccess(
                        null,
                        dto.getBucketName(),
                        dto.getFileName(),
                        dto.getFolderURL()
                );

        return fileStorageService.getResourceForViewByInputStream(fileInputStreamWithAccessDto, file);
    }

    @Override
    public User getAuthorOfPublicFileById(Long fileId) {
        return getPublicFileById(fileId).getUser();
    }

    @Override
    public void revokePublicFile(Long fileId) {
        File file = getPublicFileById(fileId);

        file.setIsPublic(false);
        fileRepository.save(file);
    }

    @Override
    public File getPublicFileById(Long fileId) {
        return fileRepository
                .getFileByIdAndIsPublicTrue(fileId)
                .orElseThrow(
                        () -> new NotFoundPublicFileByIdException(fileId)
                );
    }
}
