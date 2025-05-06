package org.y9nba.app.service.impl;

import jakarta.transaction.Transactional;
import org.apache.catalina.User;
import org.hibernate.Hibernate;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.y9nba.app.constant.Access;
import org.y9nba.app.dto.file.FileCreateDto;
import org.y9nba.app.dto.file.FileInputStreamWithAccessDto;
import org.y9nba.app.dto.file.FileUpdateDto;
import org.y9nba.app.dto.fileaccess.FileAccessCreateDto;
import org.y9nba.app.dto.fileaccess.FileAccessUpdateDto;
import org.y9nba.app.exception.local.NotPhysicalFileException;
import org.y9nba.app.exception.local.PhysicalFileOnUrlAlreadyException;
import org.y9nba.app.exception.local.PhysicalFilesAndEntriesNotSyncException;
import org.y9nba.app.exception.web.*;
import org.y9nba.app.model.FileAccessModel;
import org.y9nba.app.model.FileModel;
import org.y9nba.app.model.UserModel;
import org.y9nba.app.repository.FileRepository;
import org.y9nba.app.service.FileStorageService;

import java.io.File;
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
                            userModel,
                            null
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

    @Transactional
    @Override
    public FileModel uploadFileByAccess(Long userId, MultipartFile file, String bucketName, String fileName, String folderURL) {
        String url = createAbsFileURL(bucketName, fileName, folderURL);
        FileModel fileModel = findByUrl(url);

        if (!fileAccessService.hasAccessOnEdit(userId, fileModel.getId())) {
            throw new FileAccessDeniedException(Access.ACCESS_EDITOR);
        }

        UserModel author = findById(fileModel.getId()).getUser();
        UserModel collaborator = userService.getById(userId);

        Long fileId = updateExisting(
                fileModel,
                getFileUpdateDtoByFileAndUser(url, author, file.getSize()),
                author,
                collaborator
        );

        storageService.uploadFile(file, bucketName, folderURL);

        updateUsedStorageOfUser(author.getId());

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

    private Long updateExisting(FileModel file, FileUpdateDto fileUpdateDto, UserModel authorOfFile, UserModel authorOfUpdate) {
        if ((fileUpdateDto.getFileSize() - file.getFileSize()) > authorOfFile.getNotUsedStorage() && fileUpdateDto.getFileSize() > file.getFileSize())
            throw new UserNotEnoughMemoryException();

        FileModel fileModel = update(fileUpdateDto);

        auditLogService.logUpdate(authorOfUpdate == null ? authorOfFile : authorOfUpdate, fileModel);

        return fileModel.getId();
    }

    private void updateUsedStorageOfUser(Long userId) {
        userService.update(
                userId,
                findByUserId(userId).stream().mapToLong(FileModel::getFileSize).sum()
        );
    }

    @Override
    public FileInputStreamWithAccessDto downloadFile(Long userId, String fileName, String folderURL) {
        String url = createAbsFileURL(userId, fileName, folderURL);
        FileModel fileModel = findByUserIdAndUrl(userId, url);
        UserModel userModel = userService.getById(userId);

        auditLogService.logDownload(userModel, fileModel);

        return new FileInputStreamWithAccessDto(downloadFileByUserAndFile(userModel, fileModel), Access.ACCESS_AUTHOR);
    }

    @Transactional
    @Override
    public FileInputStreamWithAccessDto downloadFileByAccess(Long userId, String bucketName, String fileName, String folderURL) {
        String url = createAbsFileURL(bucketName, fileName, folderURL);
        FileModel fileModel = findByUrl(url);
        UserModel author = findById(fileModel.getId()).getUser();
        UserModel collaborator;
        Access access;

        if (userId != null) {
            collaborator = userService.getById(userId);

            if (fileModel.isPublic() && !fileAccessService.hasAccessOnRead(userId, fileModel.getId())) {
                giveAccessOnFileForUser(author, fileModel, collaborator, Access.ACCESS_READER);
            } else if (!fileModel.isPublic() && !fileAccessService.hasAccessOnRead(userId, fileModel.getId())) {
                throw new FileAccessDeniedException();
            }

            access = fileAccessService.findByUserAndFile(collaborator.getId(), fileModel.getId()).getAccessLevel();

            auditLogService.logDownload(collaborator, fileModel);
        } else {
            if (!fileModel.isPublic()) {
                throw new FileAccessDeniedException();
            }

            access = Access.ACCESS_READER;
        }

        return new FileInputStreamWithAccessDto(downloadFileByUserAndFile(author, fileModel), access);
    }

    @Transactional
    @Override
    public FileModel moveFileOnNewUrl(Long userId, String fileName, String newFolderURL, String oldFolderURL) {
        String oldUrl = createAbsFileURL(userId, fileName, oldFolderURL);
        String newUrl = createAbsFileURL(userId, fileName, newFolderURL);
        FileModel fileModel = findByUserIdAndUrl(userId, oldUrl);
        UserModel userModel = userService.getById(userId);

        if (existsByURL(newUrl)) {
            throw new FileNewUrlAlreadyException();
        }

        FileUpdateDto fileUpdateDto = getFileUpdateDtoByFileAndUser(oldUrl, userModel, newUrl);

        fileModel = tryMoveOrRenameFile(fileModel, userModel, fileUpdateDto);

        auditLogService.logMove(userModel, fileModel);

        return findById(fileUpdateDto.getId());
    }

    @Transactional
    @Override
    public FileModel copyExistingFile(Long userId, String fileName, String folderURL) {
        String fileURL = createAbsFileURL(userId, fileName, folderURL);
        FileModel fileModel = findByUserIdAndUrl(userId, fileURL);
        UserModel userModel = userService.getById(userId);
        FileCreateDto fileCreateDto = getFileCreateDtoAsCopyByFileModelAndUser(fileModel, userModel);
        Long copyFileId;

        try {
            storageService.copyFile(userModel.getBucketName(), fileModel, fileCreateDto);
            copyFileId = createNew(fileCreateDto, userModel);
        } catch (NotPhysicalFileException e) {
            deleteEntry(fileModel);
            throw new FilePhysicalNotFoundException(e.getMessage());
        } catch (PhysicalFileOnUrlAlreadyException e) {
            createNew(e.getFileCreateDto(), userModel);
            throw new FilePhysicalOnUrlException(e.getMessage());
        }

        auditLogService.logCopy(userModel, fileModel);

        return findById(copyFileId);
    }

    @Transactional
    @Override
    public FileModel renameFile(Long userId, String fileName, String newFileName, String folderURL) {
        String oldUrl = createAbsFileURL(userId, fileName, folderURL);
        String newUrl = createAbsFileURL(userId, newFileName, folderURL);
        FileModel fileModel = findByUserIdAndUrl(userId, createAbsFileURL(userId, fileName, folderURL));
        UserModel userModel = userService.getById(userId);

        if (existsByURL(newUrl)) {
            throw new FileNewUrlAlreadyException();
        }

        FileUpdateDto fileUpdateDto = getFileUpdateDtoByFileAndUser(oldUrl, userModel, newUrl, newFileName);

        fileModel = tryMoveOrRenameFile(fileModel, userModel, fileUpdateDto);

        auditLogService.logRename(userModel, fileModel);

        return findById(fileUpdateDto.getId());
    }

    private FileModel tryMoveOrRenameFile(FileModel fileModel, UserModel userModel, FileUpdateDto fileUpdateDto) {
        try {
            storageService.moveFile(userModel.getBucketName(), fileModel, fileUpdateDto);
        } catch (NotPhysicalFileException e) {
            deleteEntry(fileModel);
            throw new FilePhysicalNotFoundException(e.getMessage());
        } catch (PhysicalFileOnUrlAlreadyException e) {
            createNew(e.getFileCreateDto(), userModel);
            throw new FilePhysicalOnUrlException(e.getMessage());
        }

        fileModel = update(fileUpdateDto);
        return fileModel;
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

    @Override
    public FileModel giveAccessOnFileForUser(Long userId, String fileName, String folderURL, Long collaboratorUserId, Access access) {
        String url = createAbsFileURL(userId, fileName, folderURL);
        FileModel fileModel = findByUserIdAndUrl(userId, url);

        return giveAccessOnFileForUser(userService.getById(userId), fileModel, userService.getById(collaboratorUserId), access);
    }

    private FileModel giveAccessOnFileForUser(UserModel author, FileModel fileModel, UserModel collaboratorUser, Access access) {
        if (fileAccessService.existsByUserAndFile(collaboratorUser.getId(), fileModel.getId())) {
            FileAccessModel faModel = fileAccessService.findByUserAndFile(collaboratorUser.getId(), fileModel.getId());

            if (faModel.getAccessLevel() == access) {
                throw new FileAccessAlreadyException(collaboratorUser.getUsername());
            } else {
                FileAccessUpdateDto faUpdateDto = new FileAccessUpdateDto(faModel);
                faUpdateDto.setAccessLevel(access);
                fileAccessService.update(faUpdateDto);
            }
        } else {
            fileAccessService.save(new FileAccessCreateDto(fileModel, collaboratorUser, access));
            auditLogService.logAddAccess(author, fileModel);
        }

        return findByUrl(fileModel.getUrl());
    }

    @Override
    public FileModel revokeAccessOnFileForUser(Long userId, String fileName, String folderURL, Long collaboratorUserId) {
        String url = createAbsFileURL(userId, fileName, folderURL);
        FileModel fileModel = findByUserIdAndUrl(userId, url);

        fileAccessService.deleteByUserIdAndFileId(collaboratorUserId, fileModel.getId());

        auditLogService.logRemoveAccess(userService.getById(userId), fileModel);

        return findByUrl(url);
    }

    @Override
    public FileModel revokeAllAccessOnFile(Long userId, String fileName, String folderURL) {
        String url = createAbsFileURL(userId, fileName, folderURL);
        FileModel fileModel = findByUserIdAndUrl(userId, url);

        fileAccessService.deleteAllAccessesForFile(fileModel.getId());

        auditLogService.logRemoveAccess(userService.getById(userId), fileModel);

        return findByUrl(url);
    }

    @Override
    public FileModel makeFilePublic(Long userId, String fileName, String folderURL) {
        String url = createAbsFileURL(userId, fileName, folderURL);
        FileUpdateDto fileUpdateDto = new FileUpdateDto(findByUserIdAndUrl(userId, url));

        fileUpdateDto.makePublic();

        FileModel fileModel = update(fileUpdateDto);

        auditLogService.logMakePublic(userService.getById(userId), fileModel);

        return fileModel;
    }

    @Override
    public FileModel makeFilePrivate(Long userId, String fileName, String folderURL) {
        String url = createAbsFileURL(userId, fileName, folderURL);
        FileUpdateDto fileUpdateDto = new FileUpdateDto(findByUserIdAndUrl(userId, url));

        fileUpdateDto.makePrivate();

        FileModel fileModel = update(fileUpdateDto);

        fileAccessService.deleteAllAccessesReaderForFile(fileModel.getId());

        auditLogService.logMakePrivate(userService.getById(userId), fileModel);

        return fileModel;
    }

    private void deleteEntry(FileModel fileModel) {
        repository.delete(fileModel);
        updateUsedStorageOfUser(fileModel.getUser().getId());
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

    @Override
    public FileModel findOwnerByFileId(Long userId, String bucketName, String fileName, String folderURL) {
        FileModel fileModel = findByUrl(createAbsFileURL(bucketName, fileName, folderURL));

        if (fileAccessService.hasAccessOnRead(userId, fileModel.getId())) {
            return fileModel;
        } else {
            throw new FileAccessDeniedException();
        }
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

    @Transactional
    protected FileModel findById(Long id) {
        FileModel fileModel = repository
                .findById(id)
                .orElseThrow(
                        () -> new NotFoundEntryException("Not found file by id: " + id)
                );

        Hibernate.initialize(fileModel.getUser());

        return fileModel;
    }

    private FileModel findByUserIdAndUrl(Long userId, String url) {
        return repository
                .getFileModelByUser_IdAndUrl(userId, url)
                .orElseThrow(
                        () -> new NotFoundEntryException("Not found file by url: " + url)
                );
    }

    public FileModel findByUrl(String url) {
        return repository
                .getFileModelByUrl(url)
                .orElseThrow(
                        () -> new NotFoundEntryException("Not found file by url: " + url)
                );
    }

    public FileModel findFile(Long userId, String fileName, String folderURL) {
        String url = createAbsFileURL(userId, fileName, folderURL);
        return findByUserIdAndUrl(userId, url);
    }

    public FileModel findFile(String bucketName, String fileName, String folderURL) {
        String url = createAbsFileURL(bucketName, fileName, folderURL);
        return findByUrl(url);
    }

    @Override
    public ResponseEntity<InputStreamResource> getResourceForViewByInputStream(FileInputStreamWithAccessDto dto, FileModel fileModel) {
        return getResponseBuilderWithHeadersForInputStream(fileModel, dto.getAccess(), true).body(new InputStreamResource(dto.getInputStream()));
    }

    @Override
    public ResponseEntity<InputStreamResource> getResourceForDownloadByInputStream(FileInputStreamWithAccessDto dto, FileModel fileModel) {
        return getResponseBuilderWithHeadersForInputStream(fileModel, dto.getAccess(), false).body(new InputStreamResource(dto.getInputStream()));
    }

    private ResponseEntity.BodyBuilder getResponseBuilderWithHeadersForInputStream(FileModel fileModel, Access access, boolean cache) {
        String encodedFileName = URLEncoder.encode(fileModel.getFileName(), StandardCharsets.UTF_8).replaceAll("\\+", "%20");

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.parseMediaType(fileModel.getMimeType()));
        headers.add("X-Access-Level", access.name());

        if (cache) {
            headers.add(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"" + encodedFileName + "\"");
            headers.setCacheControl("must-revalidate, post-check=0, pre-check=0");
        } else {
            headers.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + encodedFileName + "\"");
        }

        return ResponseEntity.ok().headers(headers);
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

        return createAbsFileURL(bucketName, fileName, folderURL);
    }

    private String createAbsFileURL(String bucketName, String fileName, String folderURL) {
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

    private FileCreateDto getFileCreateDtoAsCopyByFileModelAndUser(FileModel file, UserModel userModel) {
        String urlForSearch = file.getUrl().substring(0, file.getUrl().lastIndexOf("."));
        String fileNameWithoutExt = file.getFileName().substring(0, file.getFileName().lastIndexOf("."));
        String ext = file.getFileName().substring(file.getFileName().lastIndexOf("."));

        int count = repository.getFileModelsByUser_IdAndUrlContaining(userModel.getId(), urlForSearch).size();

        return new FileCreateDto(
                fileNameWithoutExt + "(" + count + ")" + ext,
                file.getFileSize(),
                file.getMimeType(),
                urlForSearch + "(" + count + ")" + ext,
                userModel
        );
    }

    private FileUpdateDto getFileUpdateDtoByFileAndUser(String url, UserModel userModel, Long newFileSize) {
        FileUpdateDto fileUpdateDto = new FileUpdateDto(findByUserIdAndUrl(userModel.getId(), url));
        fileUpdateDto.setFileSize(newFileSize);

        return fileUpdateDto;
    }

    private FileUpdateDto getFileUpdateDtoByFileAndUser(String url, UserModel userModel, String newFileUrl) {
        FileUpdateDto fileUpdateDto = new FileUpdateDto(findByUserIdAndUrl(userModel.getId(), url));
        fileUpdateDto.setUrl(newFileUrl);

        return fileUpdateDto;
    }

    private FileUpdateDto getFileUpdateDtoByFileAndUser(String url, UserModel userModel, String newFileUrl, String newFileName) {
        FileUpdateDto fileUpdateDto = getFileUpdateDtoByFileAndUser(url, userModel, newFileUrl);
        fileUpdateDto.setFileName(newFileName);

        return fileUpdateDto;
    }
}
