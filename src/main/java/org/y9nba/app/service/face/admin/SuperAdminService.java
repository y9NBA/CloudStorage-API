package org.y9nba.app.service.face.admin;

import org.y9nba.app.dao.entity.User;
import org.y9nba.app.dto.admin.AdminCreateDto;

public interface SuperAdminService {
    User createAdmin(AdminCreateDto dto);
    void deleteAdminById(Long id);
}
