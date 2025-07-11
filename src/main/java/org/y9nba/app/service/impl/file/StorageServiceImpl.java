package org.y9nba.app.service.impl.file;

import io.minio.*;
import io.minio.messages.Item;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.y9nba.app.dto.file.FileCreateDto;
import org.y9nba.app.dto.file.FileUpdateDto;
import org.y9nba.app.exception.local.NotPhysicalFileException;
import org.y9nba.app.exception.local.PhysicalFileOnUrlAlreadyException;
import org.y9nba.app.exception.local.PhysicalFilesAndEntriesNotSyncException;
import org.y9nba.app.exception.web.file.FileNotUploadException;
import org.y9nba.app.dao.entity.File;
import org.y9nba.app.dao.entity.User;
import org.y9nba.app.exception.web.file.FileStorageException;
import org.y9nba.app.service.face.file.StorageService;

import java.io.InputStream;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class StorageServiceImpl implements StorageService {

    private final MinioClient minioClient;
    private final MinioServiceImpl minioService;

    public StorageServiceImpl(MinioClient minioClient, MinioServiceImpl minioService) {
        this.minioClient = minioClient;
        this.minioService = minioService;
    }

    @Override
    public void uploadFile(MultipartFile file, String bucketName, String folderUrl) {
        minioService.createBucket(bucketName);

        String fileName = file.getOriginalFilename();
        String objectName;

        if (folderUrl == null) {
            objectName = fileName;
        } else {
            objectName = folderUrl + "/" + fileName;
        }

        try (InputStream inputStream = file.getInputStream()) {
            PutObjectArgs objectArgs = PutObjectArgs.builder()
                    .bucket(bucketName)
                    .object(objectName)
                    .stream(inputStream, file.getSize(), -1)
                    .contentType(file.getContentType())
                    .build();

            minioClient.putObject(objectArgs);
        } catch (Exception e) {
            throw new FileNotUploadException();
        }
    }

    @Override
    public InputStream downloadFile(String bucketName, File file) throws NotPhysicalFileException {
        minioService.createBucket(bucketName);

        String fileURL = getCorrectUrl(file.getUrl());

        if (!isFileExist(bucketName, fileURL))
            throw new NotPhysicalFileException(fileURL);

        return downloadFileByUrl(bucketName, fileURL);
    }

    private InputStream downloadFileByUrl(String bucketName, String fileURL) {
        try {
            GetObjectArgs getArgs = GetObjectArgs.builder().bucket(bucketName).object(fileURL).build();
            return minioClient.getObject(getArgs);
        } catch (Exception e) {
            throw new FileStorageException();
        }
    }

    @Override
    public void deleteFile(String bucketName, File file) {
        minioService.createBucket(bucketName);
        deleteFileByUrl(bucketName, getCorrectUrl(file.getUrl()));
    }

    @Override
    public void moveFile(String bucketName, File file, FileUpdateDto fileUpdDto) throws NotPhysicalFileException, PhysicalFileOnUrlAlreadyException {
        minioService.createBucket(bucketName);

        String oldFileURL = getCorrectUrl(file.getUrl());
        String newFileURL = getCorrectUrl(fileUpdDto.getUrl());

        checkURLs(bucketName, oldFileURL, newFileURL, new File(fileUpdDto));

        try {
            CopySource copySource = CopySource.builder().bucket(bucketName).object(oldFileURL).build();
            CopyObjectArgs copyArgs = CopyObjectArgs.builder()
                    .source(copySource)
                    .bucket(bucketName)
                    .object(newFileURL)
                    .build();
            minioClient.copyObject(copyArgs);

        } catch (Exception e) {
            throw new FileStorageException();
        }

        deleteFileByUrl(bucketName, oldFileURL);
    }

    @Override
    public void copyFile(String bucketName, File file, FileCreateDto fileCrtDto) throws NotPhysicalFileException, PhysicalFileOnUrlAlreadyException {
        minioService.createBucket(bucketName);

        String fileURL = getCorrectUrl(file.getUrl());
        String copyFileURL = getCorrectUrl(fileCrtDto.getUrl());

        checkURLs(bucketName, fileURL, copyFileURL, new File(fileCrtDto));

        try {
            CopySource copySource = CopySource.builder().bucket(bucketName).object(fileURL).build();
            CopyObjectArgs copyArgs = CopyObjectArgs.builder()
                    .source(copySource)
                    .bucket(bucketName)
                    .object(copyFileURL)
                    .build();
            minioClient.copyObject(copyArgs);
        } catch (Exception e) {
            throw new FileStorageException();
        }
    }

    private void deleteFileByUrl(String bucketName, String fileURL) {
        if (!isFileExist(bucketName, fileURL)) {
            return;
        }

        try {
            RemoveObjectArgs rmvArgs = RemoveObjectArgs.builder().bucket(bucketName).object(fileURL).build();
            minioClient.removeObject(rmvArgs);
        } catch (Exception e) {
            throw new FileStorageException();
        }
    }

    private boolean isFileExist(String bucketName, String fileName) {
        return minioService.isFileExistByObjectName(bucketName, fileName);
    }

    private boolean isFileExist(String bucketName, File file) {
        return minioService.isFileExistByObjectName(bucketName, getCorrectUrl(file.getUrl()));
    }

    private void checkURLs(String bucketName, String beginFileURL, String endFileURL, File file) throws NotPhysicalFileException, PhysicalFileOnUrlAlreadyException {
        if (!isFileExist(bucketName, beginFileURL)) {
            throw new NotPhysicalFileException(beginFileURL);
        } else if (isFileExist(bucketName, endFileURL)) {
            StatObjectResponse statObjectArgs = getStatObjectArgs(bucketName, endFileURL);
            FileCreateDto fileCreateDto = new FileCreateDto(
                    file.getFileName(),
                    statObjectArgs.size(),
                    statObjectArgs.headers().get("Content-Type"),
                    file.getUrl(),
                    file.getUser()
            );

            throw new PhysicalFileOnUrlAlreadyException(endFileURL, fileCreateDto);
        }
    }


    @Override
    public void synchronizeFiles(String bucketName, Set<File> files, User user) throws PhysicalFilesAndEntriesNotSyncException {
        minioService.createBucket(bucketName);

        Set<File> fileModelsWithoutPhysicalFile = new java.util.HashSet<>();
        Set<FileCreateDto> filesWithoutEntryInDB = new java.util.HashSet<>();
        Set<String> objectNames = files
                .stream()
                .map(File::getUrl)
                .map(this::getCorrectUrl)
                .collect(Collectors.toSet());

        files.forEach(fileModel -> {
            if (!isFileExist(bucketName, fileModel)) {
                fileModelsWithoutPhysicalFile.add(fileModel);
            }
        });

        ListObjectsArgs args = ListObjectsArgs.builder().bucket(bucketName).recursive(true).build();
        minioClient.listObjects(args).forEach(result -> {
            try {
                if (!objectNames.contains(result.get().objectName()) && !result.get().isDir()) {
                    filesWithoutEntryInDB.add(new FileCreateDto(
                            getFileNameByObjectName(result.get().objectName()),
                            result.get().size(),
                            getContentTypeByObjectName(bucketName, result.get().objectName()),
                            bucketName + "/" + result.get().objectName(),
                            user
                    ));
                }
            } catch (Exception e) {
                throw new FileStorageException();
            }
        });

        if (!fileModelsWithoutPhysicalFile.isEmpty() || !filesWithoutEntryInDB.isEmpty())
            throw new PhysicalFilesAndEntriesNotSyncException(fileModelsWithoutPhysicalFile, filesWithoutEntryInDB);
    }

    @Override
    public void deleteBucket(String bucketName) {
        minioService.createBucket(bucketName);

        Iterable<Result<Item>> results = minioClient.listObjects(ListObjectsArgs.builder()
                .bucket(bucketName)
                .build());

        try {
            for (Result<Item> result : results) {
                minioClient.removeObject(RemoveObjectArgs.builder()
                        .bucket(bucketName)
                        .object(result.get().objectName())
                        .build());
            }

            minioClient.removeBucket(RemoveBucketArgs.builder()
                    .bucket(bucketName)
                    .build());
        } catch (Exception e) {
            throw new FileStorageException();
        }
    }

    @Override
    public void deleteFiles(String bucketName, Set<File> files) {
        minioService.createBucket(bucketName);

        files
                .stream()
                .map(File::getUrl)
                .map(this::getCorrectUrl)
                .forEach(
                        fileURL -> deleteFileByUrl(bucketName, fileURL)
                );
    }

    private String getCorrectUrl(String url) {
        return url.substring(url.indexOf("/") + 1);    // Отрезаю часть с названием bucket пользователя
    }

    private StatObjectResponse getStatObjectArgs(String bucketName, String fileURL) {
        try {
            StatObjectArgs args = StatObjectArgs.builder().bucket(bucketName).object(fileURL).build();
            return minioClient.statObject(args);
        } catch (Exception e) {
            throw new FileStorageException();
        }
    }

    private String getFileNameByObjectName(String objectName) {
        return objectName.substring(objectName.lastIndexOf("/") + 1);    // Вырезаю часть с названием файла
    }

    private String getContentTypeByObjectName(String bucketName, String objectName) {
        try {
            return minioClient.statObject(
                            StatObjectArgs.builder()
                                    .bucket(bucketName)
                                    .object(objectName)
                                    .build())
                    .headers()
                    .get("Content-Type");
        } catch (Exception e) {
            throw new FileStorageException();
        }
    }
}
