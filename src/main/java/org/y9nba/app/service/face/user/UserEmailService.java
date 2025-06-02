package org.y9nba.app.service.face.user;

import org.y9nba.app.dto.user.update.UserUpdateEmailDto;

public interface UserEmailService {
    String tryUpdateEmail(Long userId, UserUpdateEmailDto dto);
    String updateEmail(Long userId, String updateEmailToken);
    String rollbackEmail(Long userId, String rollbackEmailToken);
}
