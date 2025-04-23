package org.y9nba.app.service.impl;

import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.y9nba.app.constant.Access;
import org.y9nba.app.constant.Action;
import org.y9nba.app.dto.auditlog.AuditLogCreateDto;
import org.y9nba.app.dto.file.FileCreateDto;
import org.y9nba.app.dto.file.FileUpdateDto;
import org.y9nba.app.dto.fileaccess.FileAccessCreateDto;
import org.y9nba.app.exception.NotFoundEntryException;
import org.y9nba.app.model.FileModel;
import org.y9nba.app.model.UserModel;
import org.y9nba.app.repository.FileRepository;
import org.y9nba.app.service.FileStorageService;

import java.io.InputStream;
import java.util.Set;

@Service
public class FileStorageServiceImpl implements FileStorageService {

    private final FileRepository repository;
    private final UserServiceImpl userService;
    private final AuditLogServiceImpl auditLogService;
    private final FileAccessServiceImpl fileAccessService;
    private final StorageServiceImpl storageService;

    public FileStorageServiceImpl(FileRepository repository, UserServiceImpl userService, AuditLogServiceImpl auditLogService, FileAccessServiceImpl fileAccessService, StorageServiceImpl storageService) {
        this.repository = repository;
        this.userService = userService;
        this.auditLogService = auditLogService;
        this.fileAccessService = fileAccessService;
        this.storageService = storageService;
    }

    @Transactional
    @Override
    public FileModel uploadFile(String username, MultipartFile file) {
        return uploadFile(username, file, null);
    }

    @Transactional
    @Override
    public FileModel uploadFile(String username, MultipartFile file, String folderURL) {
        UserModel userModel = getUserByUsername(username);
        String url;

        storageService.uploadFile(file, userModel.getBucketName());

        if (folderURL == null) {
            url = createAbsFileURL(username, file.getOriginalFilename());
        } else {
            url = createAbsFileURL(username, folderURL, file.getOriginalFilename());
        }

        FileCreateDto fileCreateDto = getFileCreateDtoByFileAndUser(file, url, userModel);

        FileModel fileModel = this.save(fileCreateDto);

        AuditLogCreateDto auditLogCreateDto = new AuditLogCreateDto(userModel, fileModel, Action.ACTION_CREATE);
        FileAccessCreateDto fileAccessCreateDto = new FileAccessCreateDto(fileModel, userModel, Access.ACCESS_AUTHOR);

        auditLogService.save(auditLogCreateDto);
        fileAccessService.save(fileAccessCreateDto);

        return findById(fileModel.getId());
    }

    @Override
    public InputStream downloadFile(String username, String fileName) {
        UserModel userModel = userService.getByUsername(username);

        FileModel fileModel = repository
                .getFileModelsByUser_Username(username)
                .stream().filter(f -> f.getFileName().equals(fileName))
                .findFirst()
                .orElseThrow(
                        () -> new NotFoundEntryException("Файл c именем: " + fileName + " - не найден")
                );

        return storageService.downloadFile(fileName, userModel.getBucketName());
    }

    @Override
    public InputStream downloadFile(String username, String fileName, String folderURL) {
        return null;
    }

    @Override
    public FileModel save(FileCreateDto dto) {
        return repository.save(new FileModel(dto));
    }

    @Override
    public FileModel update(FileUpdateDto dto) {
        return repository.save(new FileModel(dto));
    }

    @Override
    public void deleteFile(String username, String fileName) {
        repository.delete(
                this.findByUsernameAndUrl(
                        username,
                        createAbsFileURL(
                                username, fileName
                        )
                )
        );
    }

    @Override
    public void deleteFile(String username, String fileName, String folderURL) {
        repository.delete(
                this.findByUsernameAndUrl(
                        username,
                        createAbsFileURL(
                                username, folderURL, fileName
                        )
                )
        );
    }

    @Override
    public void deleteById(Long id) {
        repository.deleteById(id);
    }

    @Override
    public FileModel findById(Long id) {
        return repository
                .findById(id)
                .orElseThrow(
                        () -> new NotFoundEntryException("Not found file by id: " + id)
                );
    }

    @Override
    public FileModel findByUsernameAndUrl(String username, String url) {
        return repository
                .getFileModelByUser_UsernameAndUrl(username, url)
                .orElseThrow(
                        () -> new NotFoundEntryException("Not found file by url: " + url)
                );
    }

    @Override
    public boolean existsByURL(String url) {
        return repository.existsByUrl(url);
    }

    @Override
    public Set<FileModel> findByUsername(String username) {
        return repository.getFileModelsByUser_Username(username);
    }

    @Override
    public Set<FileModel> findByUsernameAndFolderUrl(String username, String folderURL) {
        return repository
                .getFileModelsByUser_UsernameAndUrlContaining(
                        username,
                        getBucketNameByUsername(username) + "/" + folderURL
                );
    }

    private UserModel getUserByUsername(String username) {
        return userService.getByUsername(username);
    }

    private String getBucketNameByUsername(String username) {
        return userService.getByUsername(username).getBucketName();
    }

    private String createAbsFileURL(String username, String folderURL, String fileName) {
        String bucketName = getBucketNameByUsername(username);
        return String.format("%s/%s/%s", bucketName, folderURL, fileName);
    }

    private String createAbsFileURL(String username, String fileName) {
        String bucketName = getBucketNameByUsername(username);
        return String.format("%s/%s", bucketName, fileName);
    }

    private FileCreateDto getFileCreateDtoByFileAndUser(MultipartFile file, String url, UserModel userModel) {
        return new FileCreateDto(
                file.getOriginalFilename(),
                file.getSize(),
                file.getContentType(),
                url,
                userModel
        );
    }
}
