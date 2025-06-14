package org.y9nba.app.service.face.admin;

import org.y9nba.app.dao.entity.User;
import org.y9nba.app.dto.user.UserCreateDto;
import org.y9nba.app.exception.local.IncorrectSuperAdminInitException;

public interface SuperAdminInitService {
    User createSuperAdmin(UserCreateDto dto) throws IncorrectSuperAdminInitException;
    User getSuperAdmin();
    User updateSuperAdmin(User superAdminWithUpdates, String username, String email, String password) throws IncorrectSuperAdminInitException;
}
