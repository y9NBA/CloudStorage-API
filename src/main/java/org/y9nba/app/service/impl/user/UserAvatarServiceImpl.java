package org.y9nba.app.service.impl.user;

import io.minio.*;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.y9nba.app.dao.entity.User;
import org.y9nba.app.dao.repository.UserRepository;
import org.y9nba.app.exception.web.file.FileInRequestIsEmptyException;
import org.y9nba.app.exception.web.file.avatar.AvatarIsDeletedAlreadyException;
import org.y9nba.app.exception.web.file.avatar.AvatarNotFoundException;
import org.y9nba.app.exception.web.file.avatar.AvatarNotUploadException;
import org.y9nba.app.exception.web.file.avatar.FileNotImageException;
import org.y9nba.app.exception.web.file.FileStorageException;
import org.y9nba.app.exception.web.user.info.AvatarNotExistException;
import org.y9nba.app.service.face.user.UserAvatarService;
import org.y9nba.app.service.impl.file.MinioServiceImpl;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.UUID;
import java.util.regex.Pattern;

@Service
public class UserAvatarServiceImpl implements UserAvatarService {

    private final UserRepository userRepository;
    private final MinioClient minioClient;

    private final MinioServiceImpl minioService;

    private final static String baseUrl = "/user/search/avatar/";
    private final static String bucketName = "static";
    private final static int targetWidth = 1024;
    private final static int targetHeight = 1024;

    public UserAvatarServiceImpl(UserRepository userRepository, MinioClient minioClient, MinioServiceImpl minioService) {
        this.minioClient = minioClient;
        this.userRepository = userRepository;
        this.minioService = minioService;
    }

    @Override
    public void uploadAvatar(User user, MultipartFile file) {
        minioService.createBucket(bucketName);

        if (file.isEmpty()) {
            throw new FileInRequestIsEmptyException();
        }

        checkContentType(file.getContentType());

        String avatarName = generateAvatarName();

        try (InputStream inputStream = file.getInputStream()) {

            byte[] data = convertToValidImage(inputStream);
            InputStream inputStreamData = new ByteArrayInputStream(data);

            PutObjectArgs objectArgs = PutObjectArgs.builder()
                    .bucket(bucketName)
                    .object(avatarName)
                    .stream(inputStreamData, data.length, -1)
                    .contentType(MediaType.IMAGE_JPEG_VALUE)
                    .build();

            minioClient.putObject(objectArgs);

        } catch (Exception e) {
            throw new AvatarNotUploadException();
        }

        if (user.getAvatarUrl() != null) {
            deleteAvatar(user);
        }

        changeAvatar(user, baseUrl + avatarName);
    }

    @Override
    public void deleteAvatar(User user) {
        minioService.createBucket(bucketName);

        if (user.getAvatarUrl() == null) {
            throw new AvatarIsDeletedAlreadyException();
        } else if (!minioService.isFileExistByObjectName(bucketName, user.getAvatarName())) {
            changeAvatar(user, null);
            return;
        }

        String avatarName = user.getAvatarName();

        try {
            RemoveObjectArgs rmvArgs = RemoveObjectArgs.builder().bucket(bucketName).object(avatarName).build();
            minioClient.removeObject(rmvArgs);
        } catch (Exception e) {
            throw new FileStorageException();
        }

        changeAvatar(user, null);
    }

    @Override
    public ResponseEntity<InputStreamResource> getAvatarByUser(User user) {
        if (user.getAvatarUrl() == null) {
            throw new AvatarNotExistException();
        }

        return getAvatar(user.getAvatarName());
    }

    @Override
    public ResponseEntity<InputStreamResource> getAvatar(String avatarName) {
        minioService.createBucket(bucketName);

        if (!minioService.isFileExistByObjectName(bucketName, avatarName)) {
            throw new AvatarNotFoundException(avatarName);
        }

        GetObjectArgs getObjectArgs = GetObjectArgs.builder().bucket(bucketName).object(avatarName).build();
        StatObjectArgs statObjectArgs = StatObjectArgs.builder().bucket(bucketName).object(avatarName).build();

        MediaType contentType;
        InputStream inputStream;

        try {
            StatObjectResponse statObjectResponse = minioClient.statObject(statObjectArgs);

            contentType = MediaType.parseMediaType(statObjectResponse.contentType());
            inputStream = minioClient.getObject(getObjectArgs);

        } catch (Exception e) {
            throw new FileStorageException();
        }

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(contentType);
        headers.add(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"" + avatarName + "\"");
        headers.setCacheControl("must-revalidate, post-check=0, pre-check=0");

        return ResponseEntity.ok().headers(headers).body(new InputStreamResource(inputStream));
    }

    public void checkContentType(String contentType) {
        Pattern pattern = Pattern.compile("^image/(jpe?g|png|gif|webp|bmp|tiff)$", Pattern.CASE_INSENSITIVE);

        if (!pattern.matcher(contentType).matches()) {
            throw new FileNotImageException();
        }
    }

    private String generateAvatarName() {
        return UUID.randomUUID() + ".jpg";
    }

    private byte[] convertToValidImage(InputStream inputStream) {
        try {
            BufferedImage bufferedImage = ImageIO.read(inputStream);

            int width = Math.min(bufferedImage.getWidth(), targetWidth);
            int height = Math.min(bufferedImage.getHeight(), targetHeight) ;

            BufferedImage resizedImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
            Graphics2D g = resizedImage.createGraphics();

            g.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
            g.drawImage(bufferedImage, 0, 0, width, height, null);
            g.dispose();

            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            ImageIO.write(
                    resizedImage,
                    "jpg",
                    outputStream
            );

            return outputStream.toByteArray();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void changeAvatar(User user, String avatarUrl) {
        user.setAvatarUrl(avatarUrl);
        userRepository.save(user);
    }
}
