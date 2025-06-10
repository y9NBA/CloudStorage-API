package org.y9nba.app.service.face.user;

import org.springframework.core.io.InputStreamResource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;
import org.y9nba.app.dao.entity.User;

public interface UserAvatarService {
    void uploadAvatar(User user, MultipartFile file);
    void deleteAvatar(User user);
    ResponseEntity<InputStreamResource> getAvatarByUser(User user);
    ResponseEntity<InputStreamResource> getAvatar(String avatarName);
}
