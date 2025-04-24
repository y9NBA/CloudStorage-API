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

import java.io.File;
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
    public FileModel uploadFile(Long userId, MultipartFile file) {
        return uploadFile(userId, file, null);
    }

    @Transactional
    @Override
    public FileModel uploadFile(Long userId, MultipartFile file, String folderURL) {
        UserModel userModel = userService.getById(userId);
        String url;
        Long fileId;

        storageService.uploadFile(file, userModel.getBucketName(), folderURL);

        if (folderURL == null) {
            url = createAbsFileURL(userId, file.getOriginalFilename());
        } else {
            url = createAbsFileURL(userId, folderURL, file.getOriginalFilename());
        }

        if(repository.existsByUrl(url)) {
            fileId = updateExisting(file, url, userModel);
        } else {
            fileId = createNew(file, url, userModel);
        }

        return findById(fileId);
    }

    private Long createNew(MultipartFile file, String url, UserModel userModel) {
        FileCreateDto fileCreateDto = getFileCreateDtoByFileAndUser(file, url, userModel);

        FileModel fileModel = this.save(fileCreateDto);

        AuditLogCreateDto auditLogCreateDto = new AuditLogCreateDto(userModel, fileModel, Action.ACTION_CREATE);
        FileAccessCreateDto fileAccessCreateDto = new FileAccessCreateDto(fileModel, userModel, Access.ACCESS_AUTHOR);

        auditLogService.save(auditLogCreateDto);
        fileAccessService.save(fileAccessCreateDto);

        return fileModel.getId();
    }

    private Long updateExisting(MultipartFile file, String url, UserModel userModel) {
        FileUpdateDto fileUpdateDto = getFileUpdateDtoByFileAndUser(file, url, userModel);

        fileUpdateDto.setFileSize(file.getSize());

        FileModel fileModel = this.update(fileUpdateDto);

        AuditLogCreateDto auditLogCreateDto = new AuditLogCreateDto(userModel, fileModel, Action.ACTION_UPDATE);

        auditLogService.save(auditLogCreateDto);

        return fileModel.getId();
    }

    @Override
    public InputStream downloadFile(Long userId, String fileName) {
        UserModel userModel = userService.getById(userId);    // TODO сделать проверку доступа к файлу

        FileModel fileModel = repository
                .getFileModelsByUser_Id(userId)
                .stream().filter(f -> f.getFileName().equals(fileName))
                .findFirst()
                .orElseThrow(
                        () -> new NotFoundEntryException("Файл c именем: " + fileName + " - не найден")
                );

        return storageService.downloadFile(fileName, userModel.getBucketName());
    }

    @Override
    public InputStream downloadFile(Long userId, String fileName, String folderURL) {
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
    public void deleteFile(Long userId, String fileName) {
        repository.delete(
                this.findByUserIdAndUrl(
                        userId,
                        createAbsFileURL(
                                userId, fileName
                        )
                )
        );
    }

    @Override
    public void deleteFile(Long userId, String fileName, String folderURL) {
        repository.delete(
                this.findByUserIdAndUrl(
                        userId,
                        createAbsFileURL(
                                userId, folderURL, fileName
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
    public FileModel findByUserIdAndUrl(Long userId, String url) {
        return repository
                .getFileModelByUser_IdAndUrl(userId, url)
                .orElseThrow(
                        () -> new NotFoundEntryException("Not found file by url: " + url)
                );
    }

    @Override
    public boolean existsByURL(String url) {
        return repository.existsByUrl(url);
    }

    @Override
    public Set<FileModel> findByUserId(Long userId) {
        return repository.getFileModelsByUser_Id(userId);
    }

    @Override
    public Set<FileModel> findByUserIdAndFolderUrl(Long userId, String folderURL) {
        return repository
                .getFileModelsByUser_IdAndUrlContaining(
                        userId,
                        getBucketNameByUserId(userId) + "/" + folderURL
                );
    }

    private String getBucketNameByUserId(Long userId) {
        return userService.getById(userId).getBucketName();
    }

    private String createAbsFileURL(Long userId, String folderURL, String fileName) {
        String bucketName = getBucketNameByUserId(userId);
        return String.format("%s/%s/%s", bucketName, folderURL, fileName);
    }

    private String createAbsFileURL(Long userId, String fileName) {
        String bucketName = getBucketNameByUserId(userId);
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

    private FileUpdateDto getFileUpdateDtoByFileAndUser(MultipartFile file, String url, UserModel userModel) {
        return new FileUpdateDto(findByUserIdAndUrl(userModel.getId(), url));
    }
}
