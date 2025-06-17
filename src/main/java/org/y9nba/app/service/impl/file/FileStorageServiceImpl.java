package org.y9nba.app.service.impl.file;

import jakarta.transaction.Transactional;
import org.hibernate.Hibernate;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
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
import org.y9nba.app.exception.web.file.*;
import org.y9nba.app.exception.web.file.access.*;
import org.y9nba.app.exception.web.file.search.NotFoundFileByIdException;
import org.y9nba.app.exception.web.file.search.NotFoundFileByURLException;
import org.y9nba.app.exception.web.user.UserNotEnoughMemoryException;
import org.y9nba.app.dao.entity.FileAccess;
import org.y9nba.app.dao.entity.File;
import org.y9nba.app.dao.entity.User;
import org.y9nba.app.dao.repository.FileRepository;
import org.y9nba.app.service.face.file.FileStorageService;
import org.y9nba.app.service.impl.user.UserSearchServiceImpl;
import org.y9nba.app.service.impl.user.UserServiceImpl;
import org.y9nba.app.util.FileUtil;

import java.io.*;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

@Service
public class FileStorageServiceImpl implements FileStorageService {

    private final FileRepository repository;
    private final UserServiceImpl userService;
    private final AuditLogServiceImpl auditLogService;
    private final FileAccessServiceImpl fileAccessService;
    private final StorageServiceImpl storageService;
    private final UserSearchServiceImpl userSearchService;

    private final FileUtil fileUtil;

    public FileStorageServiceImpl(FileRepository repository, UserServiceImpl userService, AuditLogServiceImpl auditLogService, FileAccessServiceImpl fileAccessService, StorageServiceImpl storageService, UserSearchServiceImpl userSearchService, FileUtil fileUtil) {
        this.repository = repository;
        this.userService = userService;
        this.auditLogService = auditLogService;
        this.fileAccessService = fileAccessService;
        this.storageService = storageService;
        this.userSearchService = userSearchService;
        this.fileUtil = fileUtil;
    }

    @CacheEvict(value = {
            "FileStorageService::findFileByFullUrl",
            "FileStorageService::findFile",
            "FileStorageService::findByUrl",
            "FileStorageService::findByUserIdAndFolderUrl",
            "FileStorageService::findOwnerByUserId",
            "FileStorageService::findOwnerByUserIdAndFolderUrl",
            "FileStorageService::findOwnerByBucketNameAndFileNameAndFolderUrl"

    }, allEntries = true)
    @Transactional
    @Override
    public File uploadFile(Long userId, MultipartFile file, String folderURL) {
        User user = userService.getById(userId);
        String url = createAbsFileURL(userId, file.getOriginalFilename(), folderURL);
        Long fileId;

        if (file.isEmpty()) {
            throw new FileInRequestIsEmptyException();
        }

        if (repository.existsByUrl(url)) {
            fileId =
                    updateExisting(
                            findByUserIdAndUrl(userId, url),
                            getFileUpdateDtoByFileAndUser(url, user, file.getSize()),
                            user,
                            null
                    );
        } else {
            fileId =
                    createNew(
                            getFileCreateDtoByFileAndUser(file, url, user),
                            user
                    );
        }

        storageService.uploadFile(file, user.getBucketName(), folderURL);

        updateUsedStorageOfUser(userId);

        return findById(fileId);
    }

    @Transactional
    @Override
    public Set<File> uploadFolder(Long userId, MultipartFile[] files, String folderName, String[] paths, String folderURL) {
        Set<File> uploadedFiles = new HashSet<>();
        String baseFolderURL = (folderURL == null ? "" : folderURL + "/") + folderName;

        for (int i = 0; i < files.length; i++) {
            String path = paths.length > i ? fileUtil.parsePath(paths[i]) : "";
            MultipartFile file = files[i];

            if (!file.isEmpty()) {
                File model = uploadFile(userId, file, baseFolderURL + path);
                uploadedFiles.add(model);
            }
        }

        return uploadedFiles;
    }

    @CacheEvict(value = {
            "FileStorageService::findFileByFullUrl",
            "FileStorageService::findFile",
            "FileStorageService::findByUrl",
            "FileStorageService::findByUserIdAndFolderUrl",
            "FileStorageService::findOwnerByUserId",
            "FileStorageService::findOwnerByUserIdAndFolderUrl",
            "FileStorageService::findOwnerByBucketNameAndFileNameAndFolderUrl"

    }, allEntries = true)
    @Transactional
    @Override
    public File uploadFileByAccess(Long userId, MultipartFile file, String bucketName, String fileName, String folderURL) {
        String url = createAbsFileURL(bucketName, fileName, folderURL);
        File fileModel = findByUrl(url);

        if (!fileAccessService.hasAccessOnEdit(userId, fileModel.getId())) {
            throw new FileAccessDeniedException(Access.ACCESS_EDITOR);
        }

        User author = findById(fileModel.getId()).getUser();
        User collaborator = userService.getById(userId);

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

    private Long createNew(FileCreateDto fileCreateDto, User user) {
        if (fileCreateDto.getFileSize() > user.getNotUsedStorage())
            throw new UserNotEnoughMemoryException();

        File file = save(fileCreateDto);

        FileAccessCreateDto fileAccessCreateDto = new FileAccessCreateDto(file, user, Access.ACCESS_AUTHOR);
        fileAccessService.save(fileAccessCreateDto);

        auditLogService.logCreate(user, file);

        return file.getId();
    }

    private Long updateExisting(File file, FileUpdateDto fileUpdateDto, User authorOfFile, User authorOfUpdate) {
        if ((fileUpdateDto.getFileSize() - file.getFileSize()) > authorOfFile.getNotUsedStorage()
                && fileUpdateDto.getFileSize() > file.getFileSize())
            throw new UserNotEnoughMemoryException();

        File fileModel = update(fileUpdateDto);

        auditLogService.logUpdate(authorOfUpdate == null ? authorOfFile : authorOfUpdate, fileModel);

        return fileModel.getId();
    }

    private void updateUsedStorageOfUser(Long userId) {
        userService.update(
                userId,
                findByUserId(userId).stream().mapToLong(File::getFileSize).sum()
        );
    }

    @Override
    public FileInputStreamWithAccessDto downloadFile(Long userId, String fileName, String folderURL) {
        String url = createAbsFileURL(userId, fileName, folderURL);
        File file = findByUserIdAndUrl(userId, url);
        User user = userService.getById(userId);

        auditLogService.logDownload(user, file);

        return new FileInputStreamWithAccessDto(downloadFileByUserAndFile(user, file), Access.ACCESS_AUTHOR);
    }

    @Override
    public FileInputStreamWithAccessDto downloadFolder(Long userId, String folderURL) {
        Set<File> files = findByUserIdAndFolderUrl(userId, folderURL);
        Set<String> notExistURLs = new HashSet<>();
        User user = userService.getById(userId);
        InputStream inputStream;

        if (files.isEmpty()) {
            throw new FolderNotExistException(folderURL);
        }

        try (ByteArrayOutputStream baos = new ByteArrayOutputStream();
             ZipOutputStream zos = new ZipOutputStream(baos)) {

            for (File file : files) {
                try (InputStream fileStream = downloadFileByUserAndFile(user, file)) {
                    String fileURL = file
                            .getUrl()
                            .replaceAll(folderURL + "/", "")
                            .replaceAll(user.getBucketName() + "/", "");

                    ZipEntry zipEntry = new ZipEntry(fileURL);

                    zos.putNextEntry(zipEntry);

                    byte[] buffer = new byte[1024];
                    int len;

                    while ((len = fileStream.read(buffer)) > 0) {
                        zos.write(buffer, 0, len);
                    }

                    zos.closeEntry();

                } catch (FilePhysicalNotFoundException e) {
                    notExistURLs.add(e.getFileURL());
                }
            }

            if (!notExistURLs.isEmpty()) {
                throw new ZipNotCreatingException(notExistURLs);
            }

            zos.finish();

            inputStream = new ByteArrayInputStream(baos.toByteArray());

        } catch (IOException e) {
            throw new ZipNotCreatingException();
        }

        files.forEach(
                file -> auditLogService.logDownload(user, file)
        );

        return new FileInputStreamWithAccessDto(inputStream, Access.ACCESS_AUTHOR);
    }

    @Transactional
    @Override
    public FileInputStreamWithAccessDto downloadFileByAccess(Long userId, String bucketName, String fileName, String folderURL) {
        String url = createAbsFileURL(bucketName, fileName, folderURL);
        File file = findByUrl(url);
        User author = findById(file.getId()).getUser();
        User collaborator;
        Access access;

        if (userId != null) {
            collaborator = userSearchService.getUserById(userId);

            if (file.isPublic() && !fileAccessService.hasAccessOnRead(userId, file.getId())) {
                giveAccessOnFileForUser(author, file, collaborator, Access.ACCESS_READER);
            } else if (!file.isPublic() && !fileAccessService.hasAccessOnRead(userId, file.getId())) {
                throw new FileAccessDeniedException();
            }

            access = fileAccessService.findByUserAndFile(collaborator.getId(), file.getId()).getAccessLevel();

            auditLogService.logDownload(collaborator, file);
        } else {
            if (!file.isPublic()) {
                throw new FileAccessDeniedException();
            }

            access = Access.ACCESS_READER;
        }

        return new FileInputStreamWithAccessDto(downloadFileByUserAndFile(author, file), access);
    }

    @CacheEvict(value = {
            "FileStorageService::findFileByFullUrl",
            "FileStorageService::findFile",
            "FileStorageService::findByUrl",
            "FileStorageService::findByUserIdAndFolderUrl",
            "FileStorageService::findOwnerByUserId",
            "FileStorageService::findOwnerByUserIdAndFolderUrl",
            "FileStorageService::findOwnerByBucketNameAndFileNameAndFolderUrl"

    }, allEntries = true)
    @Transactional
    @Override
    public File moveFileOnNewUrl(Long userId, String fileName, String newFolderURL, String oldFolderURL) {
        String oldUrl = createAbsFileURL(userId, fileName, oldFolderURL);
        String newUrl = createAbsFileURL(userId, fileName, newFolderURL);
        File file = findByUserIdAndUrl(userId, oldUrl);
        User user = userService.getById(userId);

        if (existsByURL(newUrl)) {
            throw new FileNewUrlAlreadyException();
        }

        FileUpdateDto fileUpdateDto = getFileUpdateDtoByFileAndUser(oldUrl, user, newUrl);

        file = tryMoveOrRenameFile(file, user, fileUpdateDto);

        auditLogService.logMove(user, file);

        return findById(fileUpdateDto.getId());
    }

    @Transactional
    @Override
    public Set<File> moveFolderOnNewUrl(Long userId, String oldFolderURL, String newFolderURL) {
        Set<File> files = findByUserIdAndFolderUrl(userId, oldFolderURL);
        String folderName = oldFolderURL.substring(oldFolderURL.lastIndexOf("/") + 1);
        String finalFolderURL = newFolderURL != null ? newFolderURL + "/" + folderName : folderName;

        if (!findByUserIdAndFolderUrl(userId, newFolderURL).isEmpty()) {
            throw new FolderNewUrlAlreadyException();
        }

        return files
                .stream()
                .map(
                        file -> moveFileOnNewUrl(userId, file.getFileName(), oldFolderURL, finalFolderURL)
                ).collect(Collectors.toSet());
    }

    @CacheEvict(value = {
            "FileStorageService::findFileByFullUrl",
            "FileStorageService::findFile",
            "FileStorageService::findByUrl",
            "FileStorageService::findByUserIdAndFolderUrl",
            "FileStorageService::findOwnerByUserId",
            "FileStorageService::findOwnerByUserIdAndFolderUrl",
            "FileStorageService::findOwnerByBucketNameAndFileNameAndFolderUrl"

    }, allEntries = true)
    @Transactional
    @Override
    public File copyExistingFile(Long userId, String fileName, String folderURL) {
        String fileURL = createAbsFileURL(userId, fileName, folderURL);
        File file = findByUserIdAndUrl(userId, fileURL);
        User user = userService.getById(userId);
        FileCreateDto fileCreateDto = getFileCreateDtoAsCopyByFileModelAndUser(file, user);
        Long copyFileId;

        try {
            copyFileId = createNew(fileCreateDto, user);
            storageService.copyFile(user.getBucketName(), file, fileCreateDto);
        } catch (NotPhysicalFileException e) {
            deleteEntry(file);
            throw new FilePhysicalNotFoundException(e.getMessage());
        } catch (PhysicalFileOnUrlAlreadyException e) {
            createNew(e.getFileCreateDto(), user);
            throw new FilePhysicalOnUrlException(e.getMessage());
        }

        auditLogService.logCopy(user, file);

        return findById(copyFileId);
    }

    @CacheEvict(value = {
            "FileStorageService::findFileByFullUrl",
            "FileStorageService::findFile",
            "FileStorageService::findByUrl",
            "FileStorageService::findByUserIdAndFolderUrl",
            "FileStorageService::findOwnerByUserId",
            "FileStorageService::findOwnerByUserIdAndFolderUrl",
            "FileStorageService::findOwnerByBucketNameAndFileNameAndFolderUrl"

    }, allEntries = true)
    @Transactional
    @Override
    public File renameFile(Long userId, String fileName, String newFileName, String folderURL) {
        String oldUrl = createAbsFileURL(userId, fileName, folderURL);
        String newUrl = createAbsFileURL(userId, newFileName, folderURL);
        File file = findByUserIdAndUrl(userId, createAbsFileURL(userId, fileName, folderURL));
        User user = userService.getById(userId);

        if (existsByURL(newUrl)) {
            throw new FileNewUrlAlreadyException();
        }

        FileUpdateDto fileUpdateDto = getFileUpdateDtoByFileAndUser(oldUrl, user, newUrl, newFileName);

        file = tryMoveOrRenameFile(file, user, fileUpdateDto);

        auditLogService.logRename(user, file);

        return findById(fileUpdateDto.getId());
    }

    @Transactional
    @Override
    public Set<File> renameFolder(Long userId, String folderURL, String newFolderName) {
        Set<File> files = findByUserIdAndFolderUrl(userId, folderURL);
        String newFolderURL = folderURL.substring(0, folderURL.lastIndexOf("/") + 1) + newFolderName;

        if (!findByUserIdAndFolderUrl(userId, newFolderURL).isEmpty()) {
            throw new FolderNewUrlAlreadyException();
        }

        return files
                .stream()
                .map(
                        file -> moveFileOnNewUrl(userId, file.getFileName(), newFolderURL, folderURL)
                ).collect(Collectors.toSet());
    }

    private File tryMoveOrRenameFile(File file, User user, FileUpdateDto fileUpdateDto) {
        try {
            storageService.moveFile(user.getBucketName(), file, fileUpdateDto);
        } catch (NotPhysicalFileException e) {
            deleteEntry(file);
            throw new FilePhysicalNotFoundException(e.getMessage());
        } catch (PhysicalFileOnUrlAlreadyException e) {
            createNew(e.getFileCreateDto(), user);
            throw new FilePhysicalOnUrlException(e.getMessage());
        }

        file = update(fileUpdateDto);
        return file;
    }

    private InputStream downloadFileByUserAndFile(User user, File file) {
        try {
            return storageService.downloadFile(user.getBucketName(), file);
        } catch (NotPhysicalFileException e) {
            deleteEntry(file);
            throw new FilePhysicalNotFoundException(e.getMessage());
        }
    }

    @CacheEvict(value = {
            "FileStorageService::findFileByFullUrl",
            "FileStorageService::findFile",
            "FileStorageService::findByUrl",
            "FileStorageService::findByUserIdAndFolderUrl",
            "FileStorageService::findOwnerByUserId",
            "FileStorageService::findOwnerByUserIdAndFolderUrl",
            "FileStorageService::findOwnerByBucketNameAndFileNameAndFolderUrl"

    }, allEntries = true)
    @Override
    public File save(FileCreateDto dto) {
        return repository.save(new File(dto));
    }

    @CacheEvict(value = {
            "FileStorageService::findFileByFullUrl",
            "FileStorageService::findFile",
            "FileStorageService::findByUrl",
            "FileStorageService::findByUserIdAndFolderUrl",
            "FileStorageService::findOwnerByUserId",
            "FileStorageService::findOwnerByUserIdAndFolderUrl",
            "FileStorageService::findOwnerByBucketNameAndFileNameAndFolderUrl"

    }, allEntries = true)
    @Override
    public File update(FileUpdateDto dto) {
        return repository.save(new File(dto));
    }

    @Override
    public String deleteFile(Long userId, String fileName, String folderURL) {
        String url = createAbsFileURL(userId, fileName, folderURL);
        File file = findByUserIdAndUrl(userId, url);

        deleteEntry(file);
        storageService.deleteFile(getBucketNameByUserId(userId), file);

        updateUsedStorageOfUser(userId);

        return url;
    }

    @Override
    public String deleteFilesByFolder(Long userId, String folderURL) {
        refreshFiles(userId);

        Set<File> files = findByUserIdAndFolderUrl(userId, folderURL);
        String bucketName = getBucketNameByUserId(userId);

        storageService.deleteFiles(bucketName, files);
        files.forEach(this::deleteEntry);

        updateUsedStorageOfUser(userId);

        return bucketName + "/" + folderURL;
    }

    @Override
    public void deleteAllFilesByDeletedUserId(Long userId) {
        Set<File> allFiles = findByUserId(userId);

        storageService.deleteBucket(
                getBucketNameByUserId(userId)
        );

        allFiles.forEach(this::deleteEntry);
    }

    @Override
    public File giveAccessOnFileForUser(Long userId, String fileName, String folderURL, Long collaboratorUserId, Access access) {
        String url = createAbsFileURL(userId, fileName, folderURL);
        File file = findByUserIdAndUrl(userId, url);

        return giveAccessOnFileForUser(userService.getById(userId), file, userSearchService.getUserById(collaboratorUserId), access);
    }

    @CacheEvict(value = {
            "FileStorageService::findFileByFullUrl",
            "FileStorageService::findFile",
            "FileStorageService::findByUrl",
            "FileStorageService::findByUserIdAndFolderUrl",
            "FileStorageService::findOwnerByUserId",
            "FileStorageService::findOwnerByUserIdAndFolderUrl",
            "FileStorageService::findOwnerByBucketNameAndFileNameAndFolderUrl"

    }, allEntries = true)
    public File giveAccessOnFileForUser(User author, File file, User collaboratorUser, Access access) {
        if (author.getId().equals(collaboratorUser.getId())) {
            throw new FileAccessIsAuthorAlreadyException();
        }

        if (fileAccessService.existsByUserAndFile(collaboratorUser.getId(), file.getId())) {
            FileAccess faModel = fileAccessService.findByUserAndFile(collaboratorUser.getId(), file.getId());

            if (faModel.getAccessLevel() == access) {
                throw new FileAccessAlreadyException(collaboratorUser.getUsername());
            } else {
                FileAccessUpdateDto faUpdateDto = new FileAccessUpdateDto(faModel);
                faUpdateDto.setAccessLevel(access);
                fileAccessService.update(faUpdateDto);
            }
        } else {
            fileAccessService.save(new FileAccessCreateDto(file, collaboratorUser, access));
            auditLogService.logAddAccess(author, file);
        }

        return findByUrl(file.getUrl());
    }

    @CacheEvict(value = {
            "FileStorageService::findFileByFullUrl",
            "FileStorageService::findFile",
            "FileStorageService::findByUrl",
            "FileStorageService::findByUserIdAndFolderUrl",
            "FileStorageService::findOwnerByUserId",
            "FileStorageService::findOwnerByUserIdAndFolderUrl",
            "FileStorageService::findOwnerByBucketNameAndFileNameAndFolderUrl"

    }, allEntries = true)
    @Override
    public File revokeAccessOnFileForUser(Long userId, String fileName, String folderURL, Long collaboratorUserId) {
        String url = createAbsFileURL(userId, fileName, folderURL);
        File file = findByUserIdAndUrl(userId, url);

        if (userId.equals(collaboratorUserId)) {
            throw new FileAccessIsAuthorAlreadyException();
        }

        userSearchService.getUserById(collaboratorUserId);

        fileAccessService.deleteByUserIdAndFileId(collaboratorUserId, file.getId());

        auditLogService.logRemoveAccess(userService.getById(userId), file);

        return findByUrl(url);
    }

    @CacheEvict(value = {
            "FileStorageService::findFileByFullUrl",
            "FileStorageService::findFile",
            "FileStorageService::findByUrl",
            "FileStorageService::findByUserIdAndFolderUrl",
            "FileStorageService::findOwnerByUserId",
            "FileStorageService::findOwnerByUserIdAndFolderUrl",
            "FileStorageService::findOwnerByBucketNameAndFileNameAndFolderUrl"

    }, allEntries = true)
    @Override
    public File revokeAllAccessOnFile(Long userId, String fileName, String folderURL) {
        String url = createAbsFileURL(userId, fileName, folderURL);
        File file = findByUserIdAndUrl(userId, url);

        fileAccessService.deleteAllAccessesForFile(file.getId());

        auditLogService.logRemoveAccess(userService.getById(userId), file);

        return findByUrl(url);
    }

    @Override
    public File makeFilePublic(Long userId, String fileName, String folderURL) {
        String url = createAbsFileURL(userId, fileName, folderURL);
        FileUpdateDto fileUpdateDto = new FileUpdateDto(findByUserIdAndUrl(userId, url));

        if (fileUpdateDto.getIsPublic()) {
            throw new FileAccessPublicAlreadyException(url);
        }

        fileUpdateDto.makePublic();

        File file = update(fileUpdateDto);

        auditLogService.logMakePublic(userService.getById(userId), file);

        return file;
    }

    @Override
    public File makeFilePrivate(Long userId, String fileName, String folderURL) {
        String url = createAbsFileURL(userId, fileName, folderURL);
        FileUpdateDto fileUpdateDto = new FileUpdateDto(findByUserIdAndUrl(userId, url));

        if (!fileUpdateDto.getIsPublic()) {
            throw new FileAccessPrivateAlreadyException(url);
        }

        fileUpdateDto.makePrivate();

        File file = update(fileUpdateDto);

        fileAccessService.deleteAllAccessesReaderForFile(file.getId());

        auditLogService.logMakePrivate(userService.getById(userId), file);

        return file;
    }

    @CacheEvict(value = {
            "FileStorageService::findFileByFullUrl",
            "FileStorageService::findFile",
            "FileStorageService::findByUrl",
            "FileStorageService::findByUserIdAndFolderUrl",
            "FileStorageService::findOwnerByUserId",
            "FileStorageService::findOwnerByUserIdAndFolderUrl",
            "FileStorageService::findOwnerByBucketNameAndFileNameAndFolderUrl"

    }, allEntries = true)
    public void deleteEntry(File file) {
        repository.delete(file);
        updateUsedStorageOfUser(file.getUser().getId());
    }

    @Override
    public boolean existsByURL(String url) {
        return repository.existsByUrl(url);
    }

    @Override
    public Set<File> findByUserId(Long userId) {
        return repository.getFilesByUser_Id(userId);
    }

    @Cacheable(value = "FileStorageService::findByUserIdAndFolderUrl", key = "#userId + '-' + #folderURL")
    @Override
    public Set<File> findByUserIdAndFolderUrl(Long userId, String folderURL) {
        Set<File> files = repository.getFilesByUser_IdAndUrlContaining(
                userId,
                getBucketNameByUserId(userId) + "/" + folderURL
        );

        if (files.isEmpty()) {
            throw new FolderNotExistException(folderURL);
        }

        return files;
    }

    @Cacheable(value = "FileStorageService::findOwnerByBucketNameAndFileNameAndFolderUrl", key = "#userId" + "-" + "#bucketName" + "-" + "#fileName" + "-" + "#folderURL")
    @Override
    public File findOwnerByBucketNameAndFileNameAndFolderUrl(Long userId, String bucketName, String fileName, String folderURL) {
        File file = findByUrl(createAbsFileURL(bucketName, fileName, folderURL));

        if (fileAccessService.hasAccessOnRead(userId, file.getId())) {
            return file;
        } else {
            throw new FileAccessDeniedException();
        }
    }

    @Cacheable(value = "FileStorageService::findOwnerByUserId", key = "#userId")
    @Override
    public Set<File> findOwnerByUserId(Long userId) {
        Set<FileAccess> fileAccesses = fileAccessService.findByUser(userId);
        return fileAccesses
                .stream()
                .filter(fa -> fa.getAccessLevel() != Access.ACCESS_AUTHOR)
                .map(FileAccess::getFile)
                .collect(Collectors.toSet());
    }

    @Cacheable(value = "FileStorageService::findOwnerByUserIdAndFolderUrl", key = "#userId" + "-" + "#folderURL")
    @Override
    public Set<File> findOwnerByUserIdAndFolderUrl(Long userId, String folderURL) {
        Set<FileAccess> fileAccesses = fileAccessService.findByUser(userId);
        return fileAccesses
                .stream()
                .filter(fa -> fa.getAccessLevel() != Access.ACCESS_AUTHOR)
                .map(FileAccess::getFile)
                .filter(f -> f.getUrl().contains(getBucketNameByUserId(userId) + "/" + folderURL))
                .collect(Collectors.toSet());
    }

    @Transactional
    protected File findById(Long id) {
        File file = repository
                .findById(id)
                .orElseThrow(
                        () -> new NotFoundFileByIdException(id)
                );

        Hibernate.initialize(file.getUser());

        return file;
    }

    private File findByUserIdAndUrl(Long userId, String url) {
        return repository
                .getFileByUser_IdAndUrl(userId, url)
                .orElseThrow(
                        () -> new NotFoundFileByURLException(url)
                );
    }

    @Cacheable(value = "FileStorageService::findByUrl", key = "#url")
    public File findByUrl(String url) {
        return repository
                .getFileByUrl(url)
                .orElseThrow(
                        () -> new NotFoundFileByURLException(url)
                );
    }

    @Cacheable(value = "FileStorageService::findFile", key = "#userId + '-' + #fileName" + "-" + "#folderURL")
    @Transactional
    public File findFile(Long userId, String fileName, String folderURL) {
        String url = createAbsFileURL(userId, fileName, folderURL);
        return findByUserIdAndUrl(userId, url);
    }

    @Cacheable(value = "FileStorageService::findFileByFullUrl", key = "#bucketName" + "-" + "#fileName" + "-" + "#folderURL")
    public File findFile(String bucketName, String fileName, String folderURL) {
        String url = createAbsFileURL(bucketName, fileName, folderURL);
        return findByUrl(url);
    }

    @Override
    public ResponseEntity<InputStreamResource> getResourceForViewByInputStream(FileInputStreamWithAccessDto dto, File file) {
        return getResponseBuilderWithHeadersForInputStream(file.getFileName(), file.getMimeType(), dto.getAccess(), true)
                .body(new InputStreamResource(dto.getInputStream()));
    }

    @Override
    public ResponseEntity<InputStreamResource> getResourceForDownloadByInputStream(FileInputStreamWithAccessDto dto, File file) {
        return getResponseBuilderWithHeadersForInputStream(file.getFileName(), file.getMimeType(), dto.getAccess(), false)
                .body(new InputStreamResource(dto.getInputStream()));
    }

    @Override
    public ResponseEntity<InputStreamResource> getResourceForDownloadFolderByInputStream(FileInputStreamWithAccessDto dto, String folderName) {
        return getResponseBuilderWithHeadersForInputStream(folderName, MediaType.APPLICATION_OCTET_STREAM_VALUE, dto.getAccess(), false)
                .body(new InputStreamResource(dto.getInputStream()));
    }

    private ResponseEntity.BodyBuilder getResponseBuilderWithHeadersForInputStream(String fileName, String mimeType, Access access, boolean cache) {
        String encodedFileName = URLEncoder.encode(fileName, StandardCharsets.UTF_8).replaceAll("\\+", "%20");

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.parseMediaType(mimeType));
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
        User user = userService.getById(userId);

        try {
            storageService.synchronizeFiles(user.getBucketName(), findByUserId(userId), user);
        } catch (PhysicalFilesAndEntriesNotSyncException e) {
            processingRefreshFiles(
                    userId,
                    e.getFileModelsWithoutPhysicalFile(),
                    e.getFilesWithoutEntryInDB()
            );
        }
    }

    @CacheEvict(value = {
            "FileStorageService::findFileByFullUrl",
            "FileStorageService::findFile",
            "FileStorageService::findByUrl",
            "FileStorageService::findByUserIdAndFolderUrl",
            "FileStorageService::findOwnerByUserId",
            "FileStorageService::findOwnerByUserIdAndFolderUrl",
            "FileStorageService::findOwnerByBucketNameAndFileNameAndFolderUrl"

    }, allEntries = true)
    public void processingRefreshFiles(Long userId, Set<File> filesWithoutPhysicalFile, Set<FileCreateDto> filesWithoutEntryInDB) {
        filesWithoutPhysicalFile.forEach(this::deleteEntry);
        filesWithoutEntryInDB.forEach(fileCreateDto -> createNew(fileCreateDto, userService.getById(userId)));

        updateUsedStorageOfUser(userId);
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

    private FileCreateDto getFileCreateDtoByFileAndUser(MultipartFile file, String url, User user) {
        return new FileCreateDto(
                file.getOriginalFilename(),
                file.getSize(),
                file.getContentType(),
                url,
                user
        );
    }

    private FileCreateDto getFileCreateDtoAsCopyByFileModelAndUser(File file, User user) {
        String urlForSearch = file.getUrl().substring(0, file.getUrl().lastIndexOf("."));
        String fileNameWithoutExt = file.getFileName().substring(0, file.getFileName().lastIndexOf("."));
        String ext = file.getFileName().substring(file.getFileName().lastIndexOf("."));

        int count = repository.getFilesByUser_IdAndUrlContaining(user.getId(), urlForSearch).size();

        return new FileCreateDto(
                fileNameWithoutExt + "(" + count + ")" + ext,
                file.getFileSize(),
                file.getMimeType(),
                urlForSearch + "(" + count + ")" + ext,
                user
        );
    }

    private FileUpdateDto getFileUpdateDtoByFileAndUser(String url, User user, Long newFileSize) {
        FileUpdateDto fileUpdateDto = new FileUpdateDto(findByUserIdAndUrl(user.getId(), url));
        fileUpdateDto.setFileSize(newFileSize);

        return fileUpdateDto;
    }

    private FileUpdateDto getFileUpdateDtoByFileAndUser(String url, User user, String newFileUrl) {
        FileUpdateDto fileUpdateDto = new FileUpdateDto(findByUserIdAndUrl(user.getId(), url));
        fileUpdateDto.setUrl(newFileUrl);

        return fileUpdateDto;
    }

    private FileUpdateDto getFileUpdateDtoByFileAndUser(String url, User user, String newFileUrl, String newFileName) {
        FileUpdateDto fileUpdateDto = getFileUpdateDtoByFileAndUser(url, user, newFileUrl);
        fileUpdateDto.setFileName(newFileName);

        return fileUpdateDto;
    }
}
