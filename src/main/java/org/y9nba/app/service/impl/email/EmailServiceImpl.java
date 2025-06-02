package org.y9nba.app.service.impl;

import jakarta.mail.internet.MimeMessage;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;
import org.y9nba.app.service.face.EmailService;

import java.util.Map;

@Service
public class EmailServiceImpl implements EmailService {

    private final JavaMailSender mailSender;
    private final TemplateEngine templateEngine;

    @Value("${spring.mail.personal}")
    private String emailSenderName;
    @Value("${spring.mail.username}")
    private String emailSenderAddress;

    public EmailServiceImpl(JavaMailSenderImpl mailSender, TemplateEngine templateEngine) {
        this.mailSender = mailSender;
        this.templateEngine = templateEngine;
    }

    @Override
    @Async("emailTaskExecutor")
    public void sendActivationAccountConfirmationMessage(String email, String activationURL) {
        sendEmailWithTemplate(
                email,
                "Активация аккаунта",
                "activation_account",
                Map.of(
                        "activationURL", activationURL,
                        "emailSenderName", emailSenderName
                )
        );
    }

    @Override
    @Async("emailTaskExecutor")
    public void sendUpdateEmailConfirmationMessage(String email, String updateEmailURL) {
        sendEmailWithTemplate(
                email,
                "Подтверждение смены email",
                "update_email",
                Map.of(
                        "updateEmailURL", updateEmailURL,
                        "emailSenderName", emailSenderName
                )
        );
    }

    @Override
    @Async("emailTaskExecutor")
    public void sendAdminAccountInfoMessage(String email, String username, String password) {
        sendEmailWithTemplate(
                email,
                "Информация об аккаунте администратора",
                "admin_account_info",
                Map.of(
                        "username", username,
                        "password", password,
                        "emailSenderName", emailSenderName
                )
        );
    }

    @Override
    @Async("emailTaskExecutor")
    public void sendOAuth2AccountInfoMessage(String email, String username, String password) {
        sendEmailWithTemplate(
                email,
                "Информация об аккаунте, созданном через OAuth2",
                "oauth2_account_info",
                Map.of(
                        "username", username,
                        "password", password,
                        "emailSenderName", emailSenderName
                )
        );
    }

    @Override
    @Async("emailTaskExecutor")
    public void sendResetPasswordInfoMessage(String email, String username, String newPassword, String rollbackPasswordURL) {
        sendEmailWithTemplate(
                email,
                "Изменение пароля",
                "reset_password_info",
                Map.of(
                        "username", username,
                        "newPassword", newPassword,
                        "rollbackPasswordURL", rollbackPasswordURL,
                        "emailSenderName", emailSenderName
                )
        );
    }

    @Override
    @Async("emailTaskExecutor")
    public void sendResetPasswordInfoMessage(String email, String rollbackPasswordURL) {
        sendEmailWithTemplate(
                email,
                "Запрос на сброс пароля",
                "reset_password_info",
                Map.of(
                        "rollbackPasswordURL", rollbackPasswordURL,
                        "emailSenderName", emailSenderName
                )
        );
    }

    @Override
    @Async("emailTaskExecutor")
    public void sendResetPasswordConfirmationMessage(String email, String resetPasswordURL) {
        sendEmailWithTemplate(
                email,
                "Сброс пароля",
                "reset_password_confirmation",
                Map.of(
                        "resetPasswordURL", resetPasswordURL,
                        "emailSenderName", emailSenderName
                )
        );
    }

    @Override
    @Async("emailTaskExecutor")
    public void sendRollbackUpdateEmailConfirmationMessage(String email, String rollbackUpdateEmailURL) {
        sendEmailWithTemplate(
                email,
                "Изменение email",
                "rollback_update_email",
                Map.of(
                        "rollbackUpdateEmailURL", rollbackUpdateEmailURL,
                        "emailSenderName", emailSenderName
                )
        );
    }

    private void sendEmailWithTemplate(String to, String subject, String templateName, Map<String, Object> templateVariables) {
        Context thymeleafContext = new Context();
        thymeleafContext.setVariables(templateVariables);
        String htmlBody = templateEngine.process(templateName, thymeleafContext);

        sendEmail(to, subject, htmlBody);
    }

    @SneakyThrows
    private void sendEmail(String email, String subject, String text) {
        MimeMessage mimeMessage = mailSender.createMimeMessage();
        MimeMessageHelper messageHelper = new MimeMessageHelper(mimeMessage, true, "UTF-8");
        messageHelper.setFrom(emailSenderAddress, emailSenderName);
        messageHelper.setTo(email);
        messageHelper.setSubject(subject);
        messageHelper.setText(text, true);
        mailSender.send(mimeMessage);
    }
}
