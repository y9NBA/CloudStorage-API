package org.y9nba.app.service.face.user;

import org.y9nba.app.dao.entity.User;

import java.util.Set;
import java.util.UUID;

public interface UserSearchService {
    User getUserById(Long id);
    User getAdminById(Long id);
    Set<User> getAllUsers(String username, String email, UUID bucketName, Long authorizedUserId);
    Set<User> getAllActiveUsers(String username, String email, UUID bucketName, Long id);
    Set<User> getAllBannedUsers(String username, String email, UUID bucketName, Long id);
    Set<User> getAllAdmins(String username, String email, UUID bucketName, Long authorizedUserId);
}
