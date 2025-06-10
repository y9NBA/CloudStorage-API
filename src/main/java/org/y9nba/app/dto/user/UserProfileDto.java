package org.y9nba.app.dto.user;

import lombok.Getter;
import org.y9nba.app.dao.entity.User;

import java.time.LocalDateTime;

@Getter
public class UserProfileDto {
    private final Long id;
    private final String username;
    private final String email;
    private final String bucketName;
    private final String avatarUrl;
    private final Long storageLimit;
    private final Long userStorage;
    private final LocalDateTime createdAt;

    public UserProfileDto(User model) {
        this.id = model.getId();
        this.username = model.getUsername();
        this.email = model.getEmail();
        this.bucketName = model.getBucketName();
        this.avatarUrl = model.getAvatarUrl();
        this.storageLimit = model.getStorageLimit();
        this.userStorage = model.getUsedStorage();
        this.createdAt = model.getCreatedAt();
    }
}
