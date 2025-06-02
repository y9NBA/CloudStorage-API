package org.y9nba.app.service.impl.email;

import org.springframework.stereotype.Service;
import org.y9nba.app.service.face.email.PublicFileInfoService;

@Service
public class PublicFileInfoServiceImpl implements PublicFileInfoService {

    private final EmailServiceImpl emailService;

    public PublicFileInfoServiceImpl(EmailServiceImpl emailService) {
        this.emailService = emailService;
    }

    @Override
    public void sendWarningInfo(String email, String fileURL) {
        emailService.sendWarningOfPublicFileInfoMessage(email, fileURL);
    }

    @Override
    public void sendNotificationInfo(String email, String fileURL) {
        emailService.sendNotificationOfPublicFileInfoMessage(email, fileURL);
    }
}
