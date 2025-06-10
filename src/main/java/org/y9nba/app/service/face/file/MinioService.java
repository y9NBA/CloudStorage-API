package org.y9nba.app.service.face.file;

public interface MinioService {
    boolean isFileExistByObjectName(String bucketName, String objectName);
    void createBucket(String bucketName);
}
