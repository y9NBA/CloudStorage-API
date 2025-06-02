package org.y9nba.app.service.impl.email;

import org.springframework.stereotype.Service;
import org.y9nba.app.service.face.email.LockedAccountInfoService;

@Service
public class LockedAccountInfoServiceImpl implements LockedAccountInfoService {

    private final EmailServiceImpl emailService;

    public LockedAccountInfoServiceImpl(EmailServiceImpl emailService) {
        this.emailService = emailService;
    }

    @Override
    public void sendBanAccountInfo(String email, String username) {
        emailService.sendBannedInfoMessage(email, username);
    }

    @Override
    public void sendUnbanAccountInfo(String email, String username) {
        emailService.sendUnbannedInfoMessage(email, username);
    }
}
