package org.y9nba.app.initializer;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import org.y9nba.app.dto.user.UserCreateDto;
import org.y9nba.app.dao.entity.User;
import org.y9nba.app.service.impl.admin.SuperAdminInitServiceImpl;
import org.y9nba.app.util.PasswordUtil;

@Component
@Slf4j
public class SuperAdminInitializer {

    private final SuperAdminInitServiceImpl superAdminInitService;
    private final PasswordUtil passwordUtil;

    @Value("${initializer.super_admin.username}")
    private String superAdminUsername;
    @Value("${initializer.super_admin.password}")
    private String superAdminPassword;
    @Value("${initializer.super_admin.email}")
    private String superAdminEmail;

    public SuperAdminInitializer(SuperAdminInitServiceImpl superAdminInitService, PasswordUtil passwordUtil) {
        this.superAdminInitService = superAdminInitService;
        this.passwordUtil = passwordUtil;
    }

    @EventListener(ApplicationReadyEvent.class)
    public void initializeSuperAdmin() {
        User superAdmin = superAdminInitService.getSuperAdmin();

        if (superAdmin == null) {
            superAdmin = superAdminInitService.createSuperAdmin(
                    new UserCreateDto(
                            superAdminUsername,
                            superAdminEmail,
                            passwordUtil.encode(superAdminPassword)
                    )
            );

            log.info("Initializing super admin: {}", superAdmin);
        } else {
            superAdmin.setUsername(superAdminUsername);
            superAdmin.setEmail(superAdminEmail);
            superAdmin.setPassword(passwordUtil.encode(superAdminPassword));

            superAdmin = superAdminInitService.updateSuperAdmin(superAdmin);

            log.info("Updating super admin: {}", superAdmin);
        }

        log.info("Super admin password: {}", superAdminPassword);
    }
}
