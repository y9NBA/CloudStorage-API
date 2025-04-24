package org.y9nba.app.service.impl;

import io.minio.*;
import io.minio.messages.Bucket;
import lombok.SneakyThrows;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.y9nba.app.exception.FileNotUploadException;
import org.y9nba.app.model.FileModel;
import org.y9nba.app.service.StorageService;

import java.io.InputStream;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class StorageServiceImpl implements StorageService {

    private final MinioClient minioClient;

    public StorageServiceImpl(MinioClient minioClient) {
        this.minioClient = minioClient;
    }

    @Override
    public void uploadFile(MultipartFile file, String bucketName) {
        uploadFile(file, bucketName, null);
    }

    @Override
    public void uploadFile(MultipartFile file, String bucketName, String folderUrl) {
        String fileName = file.getOriginalFilename();
        InputStream inputStream;
        String objectName;

        if (folderUrl == null) {
            objectName = fileName;
        } else {
            objectName = folderUrl + "/" + fileName;
        }

        createBucket(bucketName);

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
    public InputStream downloadFile(String bucketName, FileModel fileModel) {
        return downloadFileByUrl(bucketName, getCorrectUrl(fileModel.getUrl()));
    }

    @Override
    public InputStream downloadFile(String bucketName, String fileName) {
        return downloadFileByUrl(bucketName, fileName);
    }

    @Override
    public InputStream downloadFileByUrl(String bucketName, String fileURL) {
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

    public void createBucket(String bucketName) {
        try {
            if (!bucketExists(bucketName))  {
                minioClient.makeBucket(MakeBucketArgs.builder().bucket(bucketName).build());
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public List<String> getAllBucketsName() {
        try {
            return minioClient.listBuckets().stream().map(Bucket::name).collect(Collectors.toList());
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    @SneakyThrows
    public boolean bucketExists(String bucketName) {
        return minioClient.bucketExists(BucketExistsArgs.builder().bucket(bucketName).build());
    }

    private String getCorrectUrl(String url) {
        return url.substring(url.indexOf("/") + 1);    // Отрезаю часть с названием bucket пользователя
    }
}
