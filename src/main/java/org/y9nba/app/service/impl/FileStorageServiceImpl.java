package org.y9nba.app.service.impl;

import jakarta.transaction.Transactional;
import org.hibernate.Hibernate;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.y9nba.app.constant.Access;
import org.y9nba.app.dto.file.FileCreateDto;
import org.y9nba.app.dto.file.FileUpdateDto;
import org.y9nba.app.dto.fileaccess.FileAccessCreateDto;
import org.y9nba.app.dto.share.ExpireRequestDto;
import org.y9nba.app.dto.share.SharedUrlResponseDto;
import org.y9nba.app.exception.local.NotPhysicalFileException;
import org.y9nba.app.exception.local.PhysicalFileOnNewUrlAlreadyException;
import org.y9nba.app.exception.local.PhysicalFilesAndEntriesNotSyncException;
import org.y9nba.app.exception.web.*;
import org.y9nba.app.model.FileAccessModel;
import org.y9nba.app.model.FileModel;
import org.y9nba.app.model.UserModel;
import org.y9nba.app.repository.FileRepository;
import org.y9nba.app.service.FileStorageService;

import java.io.InputStream;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Set;
import java.util.stream.Collectors;

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
    public FileModel uploadFile(Long userId, MultipartFile file, String folderURL) {
        UserModel userModel = userService.getById(userId);
        String url = createAbsFileURL(userId, file.getOriginalFilename(), folderURL);
        Long fileId;

        if (repository.existsByUrl(url)) {
            fileId =
                    updateExisting(
                            findByUserIdAndUrl(userId, url),
                            getFileUpdateDtoByFileAndUser(url, userModel, file.getSize()),
                            userModel
                    );
        } else {
            fileId =
                    createNew(
                            getFileCreateDtoByFileAndUser(file, url, userModel),
                            userModel
                    );
        }

        storageService.uploadFile(file, userModel.getBucketName(), folderURL);

        updateUsedStorageOfUser(userId);

        return findById(fileId);
    }

    private Long createNew(FileCreateDto fileCreateDto, UserModel userModel) {
        if (fileCreateDto.getFileSize() > userModel.getNotUsedStorage())
            throw new UserNotEnoughMemoryException();

        FileModel fileModel = save(fileCreateDto);

        FileAccessCreateDto fileAccessCreateDto = new FileAccessCreateDto(fileModel, userModel, Access.ACCESS_AUTHOR);
        fileAccessService.save(fileAccessCreateDto);

        auditLogService.logCreate(userModel, fileModel);

        return fileModel.getId();
    }

    private Long updateExisting(FileModel file, FileUpdateDto fileUpdateDto, UserModel userModel) {
        if ((fileUpdateDto.getFileSize() - file.getFileSize()) > userModel.getNotUsedStorage() && fileUpdateDto.getFileSize() > file.getFileSize())
            throw new UserNotEnoughMemoryException();

        FileModel fileModel = update(fileUpdateDto);

        auditLogService.logUpdate(userModel, fileModel);

        return fileModel.getId();
    }

    private void updateUsedStorageOfUser(Long userId) {
        userService.update(
                userId,
                findByUserId(userId).stream().mapToLong(FileModel::getFileSize).sum()
        );
    }

    @Override
    public InputStream downloadFile(Long userId, String fileName, String folderURL) {
        String url = createAbsFileURL(userId, fileName, folderURL);
        FileModel fileModel = findByUserIdAndUrl(userId, url);
        UserModel userModel = userService.getById(userId);

        auditLogService.logDownload(userModel, fileModel);

        return downloadFileByUserAndFile(userModel, fileModel);
    }

    @Transactional
    @Override
    public InputStream downloadFileByAccess(Long userId, Long fileId) {
        checkAccessOnRead(userId, fileId);

        FileModel fileModel = findById(fileId);

        UserModel author = fileModel.getUser();

        auditLogService.logDownload(userService.getById(userId), fileModel);

        return downloadFileByUserAndFile(author, fileModel);
    }

    @Transactional
    @Override
    public FileModel moveFileOnNewUrl(Long userId, String fileName, String newFolderURL, String oldFolderURL) {
        String oldUrl = createAbsFileURL(userId, fileName, oldFolderURL);
        String newUrl = createAbsFileURL(userId, fileName, newFolderURL);
        FileModel fileModel = findByUserIdAndUrl(userId, oldUrl);
        UserModel userModel = userService.getById(userId);
        FileUpdateDto fileUpdateDto = new FileUpdateDto(fileModel);

        if (existsByURL(newUrl)) {
            throw new FileNewUrlAlreadyException();
        }

        fileUpdateDto.setUrl(newUrl);

        try {
            storageService.moveFile(userModel.getBucketName(), fileModel, fileUpdateDto);
        } catch (NotPhysicalFileException e) {
            deleteEntry(fileModel);
            throw new FilePhysicalNotFoundException(e.getMessage());
        } catch (PhysicalFileOnNewUrlAlreadyException e) {
            createNew(e.getFileCreateDto(), userModel);
            throw new FilePhysicalOnNewUrlException(e.getMessage());
        }

        fileModel = update(fileUpdateDto);

        auditLogService.logMove(userModel, fileModel);

        return findById(fileUpdateDto.getId());
    }

    private InputStream downloadFileByUserAndFile(UserModel userModel, FileModel fileModel) {
        try {
            return storageService.downloadFile(userModel.getBucketName(), fileModel);
        } catch (NotPhysicalFileException e) {
            deleteEntry(fileModel);
            throw new FilePhysicalNotFoundException(e.getMessage());
        }
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
    public String deleteFile(Long userId, String fileName, String folderURL) {
        String url = createAbsFileURL(userId, fileName, folderURL);
        FileModel fileModel = findByUserIdAndUrl(userId, url);

        deleteEntry(fileModel);
        storageService.deleteFile(getBucketNameByUserId(userId), fileModel);

        return url;
    }

    private void deleteEntry(FileModel fileModel) {
        repository.delete(fileModel);
        updateUsedStorageOfUser(fileModel.getUser().getId());
    }

    @Transactional
    @Override
    public FileModel findById(Long id) {
        FileModel fileModel = repository
                .findById(id)
                .orElseThrow(
                        () -> new NotFoundEntryException("Not found file by id: " + id)
                );

        Hibernate.initialize(fileModel.getUser());

        return fileModel;
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
    public FileModel findFile(Long userId, String fileName, String folderURL) {
        String url = createAbsFileURL(userId, fileName, folderURL);
        return findByUserIdAndUrl(userId, url);
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

    @Transactional
    @Override
    public FileModel findOwnerByFileId(Long userId, Long fileId) {
        checkAccessOnRead(userId, fileId);

        return findById(fileId);
    }

    @Override
    public Set<FileModel> findOwnerByUserId(Long userId) {
        Set<FileAccessModel> fileAccesses = fileAccessService.findByUser(userId);
        return fileAccesses
                .stream()
                .filter(fa -> fa.getAccessLevel() != Access.ACCESS_AUTHOR)
                .map(FileAccessModel::getFile)
                .collect(Collectors.toSet());
    }

    @Override
    public Set<FileModel> findOwnerByUserIdAndFolderUrl(Long userId, String folderURL) {
        Set<FileAccessModel> fileAccesses = fileAccessService.findByUser(userId);
        return fileAccesses
                .stream()
                .filter(fa -> fa.getAccessLevel() != Access.ACCESS_AUTHOR)
                .map(FileAccessModel::getFile)
                .filter(f -> f.getUrl().contains(getBucketNameByUserId(userId) + "/" + folderURL))
                .collect(Collectors.toSet());
    }

    @Override
    public SharedUrlResponseDto getSharedUrlForFile(ExpireRequestDto expireRequestDto, Long userId, String fileName, String folderURL) {
        FileModel fileModel = findByUserIdAndUrl(userId, createAbsFileURL(userId, fileName, folderURL));

        try {
            String sharedUrl = storageService.shareFile(getBucketNameByUserId(userId), fileModel, expireRequestDto.calcExpireTime());
            return new SharedUrlResponseDto(sharedUrl, expireRequestDto.calcExpireTime());
        } catch (NotPhysicalFileException e) {
            deleteEntry(fileModel);
            throw new FilePhysicalNotFoundException(e.getMessage());
        }
    }

    @Override
    public ResponseEntity<InputStreamResource> getResourceByInputStream(InputStream inputStream, String fileName) {
        String encodedFileName = URLEncoder.encode(fileName, StandardCharsets.UTF_8).replaceAll("\\+", "%20");

        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + encodedFileName + "\"");
        headers.add(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_OCTET_STREAM_VALUE);

        return ResponseEntity.ok().headers(headers).body(new InputStreamResource(inputStream));
    }

    @Override
    public void refreshFiles(Long userId) {
        UserModel userModel = userService.getById(userId);

        try {
            storageService.synchronizeFile(userModel.getBucketName(), findByUserId(userId), userModel);
        } catch (PhysicalFilesAndEntriesNotSyncException e) {
            e.getFileModelsWithoutPhysicalFile().forEach(this::deleteEntry);
            e.getFilesWithoutEntryInDB().forEach(fileCreateDto -> {
                createNew(fileCreateDto, userModel);
            });

            updateUsedStorageOfUser(userId);
        }
    }

    private String getBucketNameByUserId(Long userId) {
        return userService.getById(userId).getBucketName();
    }

    private String createAbsFileURL(Long userId, String fileName, String folderURL) {
        String bucketName = getBucketNameByUserId(userId);

        if (folderURL == null) {
            return String.format("%s/%s", bucketName, fileName);
        } else {
            return String.format("%s/%s/%s", bucketName, folderURL, fileName);
        }
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

    private FileUpdateDto getFileUpdateDtoByFileAndUser(String url, UserModel userModel, Long newFileSize) {
        FileUpdateDto fileUpdateDto = new FileUpdateDto(findByUserIdAndUrl(userModel.getId(), url));
        fileUpdateDto.setFileSize(newFileSize);

        return fileUpdateDto;
    }

    private void checkAccessOnRead(Long userId, Long fileId) {
        if (!fileAccessService.hasAccessOnRead(userId, fileId)) {
            throw new FileAccessDeniedException();
        }
    }
}
