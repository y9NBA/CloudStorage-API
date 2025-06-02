package org.y9nba.app.service.impl.email;

import org.springframework.stereotype.Service;
import org.y9nba.app.service.face.email.AccountInfoService;

@Service
public class AccountInfoServiceImpl implements AccountInfoService {

    private final EmailServiceImpl emailService;

    public AccountInfoServiceImpl(EmailServiceImpl emailService) {
        this.emailService = emailService;
    }

    @Override
    public void sendAdminAccountInfo(String email, String username, String password) {
        emailService.sendAdminAccountInfoMessage(email, username, password);
    }

    @Override
    public void sendUserOAuth2AccountInfo(String email, String username, String password) {
        emailService.sendOAuth2AccountInfoMessage(email, username, password);
    }
}
