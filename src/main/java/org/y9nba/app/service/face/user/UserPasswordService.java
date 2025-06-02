package org.y9nba.app.service.face.user;

import org.y9nba.app.dto.user.update.UserResetPasswordDto;

public interface UserPasswordService {
    String resetPasswordByEmail(String email);
    String resetPassword(Long userId, UserResetPasswordDto dto, String resetPasswordToken);
    String rollbackPassword(Long userId, String rollbackPasswordToken);
}
