package org.y9nba.app.service.face.admin;

import org.y9nba.app.dao.entity.User;
import org.y9nba.app.dto.user.UserCreateDto;

public interface SuperAdminInitService {
    User createSuperAdmin(UserCreateDto dto);
    User getSuperAdmin();
    User updateSuperAdmin(User superAdminWithUpdates);
}
