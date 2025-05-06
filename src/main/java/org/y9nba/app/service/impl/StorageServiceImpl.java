package org.y9nba.app.service.impl;

import io.minio.*;
import io.minio.http.Method;
import io.minio.messages.Bucket;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.y9nba.app.dto.file.FileCreateDto;
import org.y9nba.app.dto.file.FileUpdateDto;
import org.y9nba.app.exception.local.NotPhysicalFileException;
import org.y9nba.app.exception.local.PhysicalFileOnUrlAlreadyException;
import org.y9nba.app.exception.local.PhysicalFilesAndEntriesNotSyncException;
import org.y9nba.app.exception.web.FileNotUploadException;
import org.y9nba.app.model.FileModel;
import org.y9nba.app.model.UserModel;
import org.y9nba.app.service.StorageService;

import java.io.InputStream;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class StorageServiceImpl implements StorageService {

    private final MinioClient minioClient;

    public StorageServiceImpl(MinioClient minioClient) {
        this.minioClient = minioClient;
    }

    @Override
    public void uploadFile(MultipartFile file, String bucketName, String folderUrl) {
        createBucket(bucketName);

        String fileName = file.getOriginalFilename();
        InputStream inputStream;
        String objectName;

        if (folderUrl == null) {
            objectName = fileName;
        } else {
            objectName = folderUrl + "/" + fileName;
        }

        try {
            inputStream = file.getInputStream();

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
    public InputStream downloadFile(String bucketName, FileModel fileModel) throws NotPhysicalFileException {
        createBucket(bucketName);

        String fileURL = getCorrectUrl(fileModel.getUrl());

        if (!isFileExist(bucketName, fileURL))
            throw new NotPhysicalFileException(fileURL);

        return downloadFileByUrl(bucketName, fileURL);
    }

    private InputStream downloadFileByUrl(String bucketName, String fileURL) {
        try {
            GetObjectArgs getArgs = GetObjectArgs.builder().bucket(bucketName).object(fileURL).build();
            return minioClient.getObject(getArgs);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void downloadManyFiles(String username, List<FileModel> fileModels) {

    }

    @Override
    public void deleteFile(String bucketName, FileModel fileModel) {
        createBucket(bucketName);
        deleteFileByUrl(bucketName, getCorrectUrl(fileModel.getUrl()));
    }

    @Override
    public void moveFile(String bucketName, FileModel fileModel, FileUpdateDto fileUpdDto) throws NotPhysicalFileException, PhysicalFileOnUrlAlreadyException {
        createBucket(bucketName);

        String oldFileURL = getCorrectUrl(fileModel.getUrl());
        String newFileURL = getCorrectUrl(fileUpdDto.getUrl());

        checkURLs(bucketName, oldFileURL, newFileURL, new FileModel(fileUpdDto));

        try {
            CopySource copySource = CopySource.builder().bucket(bucketName).object(oldFileURL).build();
            CopyObjectArgs copyArgs = CopyObjectArgs.builder()
                    .source(copySource)
                    .bucket(bucketName)
                    .object(newFileURL)
                    .build();
            minioClient.copyObject(copyArgs);

            deleteFileByUrl(bucketName, oldFileURL);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void copyFile(String bucketName, FileModel fileModel, FileCreateDto fileCrtDto) throws NotPhysicalFileException, PhysicalFileOnUrlAlreadyException {
        createBucket(bucketName);

        String fileURL = getCorrectUrl(fileModel.getUrl());
        String copyFileURL = getCorrectUrl(fileCrtDto.getUrl());

        checkURLs(bucketName, fileURL, copyFileURL,  new FileModel(fileCrtDto));

        try {
            CopySource copySource = CopySource.builder().bucket(bucketName).object(fileURL).build();
            CopyObjectArgs copyArgs = CopyObjectArgs.builder()
                    .source(copySource)
                    .bucket(bucketName)
                    .object(copyFileURL)
                    .build();
            minioClient.copyObject(copyArgs);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private void deleteFileByUrl(String bucketName, String fileURL) {
        try {
            RemoveObjectArgs rmvArgs = RemoveObjectArgs.builder().bucket(bucketName).object(fileURL).build();
            minioClient.removeObject(rmvArgs);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private boolean isFileExist(String bucketName, String fileName) {
        return isFileExistByUrl(bucketName, fileName);
    }

    private boolean isFileExist(String bucketName, FileModel fileModel) {
        return isFileExistByUrl(bucketName, getCorrectUrl(fileModel.getUrl()));
    }

    private void checkURLs(String bucketName, String beginFileURL, String endFileURL, FileModel fileModel) throws NotPhysicalFileException, PhysicalFileOnUrlAlreadyException {
        if (!isFileExist(bucketName, beginFileURL)) {
            throw new NotPhysicalFileException(beginFileURL);
        } else if (isFileExist(bucketName, endFileURL)) {
            StatObjectResponse statObjectArgs = getStatObjectArgs(bucketName, endFileURL);
            FileCreateDto fileCreateDto = new FileCreateDto(
                    fileModel.getFileName(),
                    statObjectArgs.size(),
                    statObjectArgs.headers().get("Content-Type"),
                    fileModel.getUrl(),
                    fileModel.getUser()
            );

            throw new PhysicalFileOnUrlAlreadyException(endFileURL, fileCreateDto);
        }
    }


    @Override
    public void synchronizeFile(String bucketName, Set<FileModel> fileModels, UserModel user) throws PhysicalFilesAndEntriesNotSyncException {
        createBucket(bucketName);

        Set<FileModel> fileModelsWithoutPhysicalFile = new java.util.HashSet<>();
        Set<FileCreateDto> filesWithoutEntryInDB = new java.util.HashSet<>();
        Set<String> objectNames = fileModels.stream().map(FileModel::getUrl).map(this::getCorrectUrl).collect(Collectors.toSet());

        fileModels.forEach(fileModel -> {
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
                throw new RuntimeException(e);
            }
        });

        if (!fileModelsWithoutPhysicalFile.isEmpty() || !filesWithoutEntryInDB.isEmpty())
            throw new PhysicalFilesAndEntriesNotSyncException(fileModelsWithoutPhysicalFile, filesWithoutEntryInDB);
    }

    private boolean isFileExistByUrl(String bucketName, String fileURL) {
        try {
            return minioClient.statObject(StatObjectArgs.builder().bucket(bucketName).object(fileURL).build()) != null;
        } catch (Exception e) {
            return false;
        }
    }

    private void createBucket(String bucketName) {
        try {
            if (!bucketExists(bucketName)) {
                minioClient.makeBucket(MakeBucketArgs.builder().bucket(bucketName).build());
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @SneakyThrows
    private boolean bucketExists(String bucketName) {
        return minioClient.bucketExists(BucketExistsArgs.builder().bucket(bucketName).build());
    }

    private String getCorrectUrl(String url) {
        return url.substring(url.indexOf("/") + 1);    // Отрезаю часть с названием bucket пользователя
    }

    private StatObjectResponse getStatObjectArgs(String bucketName, String fileURL) {
        try {
            StatObjectArgs args = StatObjectArgs.builder().bucket(bucketName).object(fileURL).build();
            return minioClient.statObject(args);
        } catch (Exception e) {
            throw new RuntimeException(e);
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
            throw new RuntimeException(e);
        }
    }
}
