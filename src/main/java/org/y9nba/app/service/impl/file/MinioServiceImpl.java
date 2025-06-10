package org.y9nba.app.service.impl.file;

import io.minio.BucketExistsArgs;
import io.minio.MakeBucketArgs;
import io.minio.MinioClient;
import io.minio.StatObjectArgs;
import org.springframework.stereotype.Service;
import org.y9nba.app.exception.web.file.FileStorageException;
import org.y9nba.app.service.face.file.MinioService;

@Service
public class MinioServiceImpl implements MinioService {

    private final MinioClient minioClient;

    public MinioServiceImpl(MinioClient minioClient) {
        this.minioClient = minioClient;
    }

    @Override
    public boolean isFileExistByObjectName(String bucketName, String objectName) {
        try {
            return minioClient.statObject(StatObjectArgs.builder().bucket(bucketName).object(objectName).build()) != null;
        } catch (Exception e) {
            return false;
        }
    }

    @Override
    public void createBucket(String bucketName) {
        try {
            if (!minioClient.bucketExists(BucketExistsArgs.builder().bucket(bucketName).build())) {
                minioClient.makeBucket(MakeBucketArgs.builder().bucket(bucketName).build());
            }
        } catch (Exception e) {
            throw new FileStorageException();
        }
    }
}
