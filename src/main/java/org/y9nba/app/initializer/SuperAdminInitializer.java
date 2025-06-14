package org.y9nba.app.initializer;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import org.y9nba.app.dto.user.UserCreateDto;
import org.y9nba.app.dao.entity.User;
import org.y9nba.app.exception.local.IncorrectSuperAdminInitException;
import org.y9nba.app.service.impl.admin.SuperAdminInitServiceImpl;
import org.y9nba.app.util.PasswordUtil;

@Component
@Slf4j
public class SuperAdminInitializer {

    private final SuperAdminInitServiceImpl superAdminInitService;

    @Value("${initializer.super_admin.username}")
    private String superAdminUsername;
    @Value("${initializer.super_admin.password}")
    private String superAdminPassword;
    @Value("${initializer.super_admin.email}")
    private String superAdminEmail;

    public SuperAdminInitializer(SuperAdminInitServiceImpl superAdminInitService) {
        this.superAdminInitService = superAdminInitService;
    }

    @EventListener(ApplicationReadyEvent.class)
    public void initializeSuperAdmin() throws IncorrectSuperAdminInitException {
        User superAdmin = superAdminInitService.getSuperAdmin();

        if (superAdmin == null) {
            superAdmin = superAdminInitService.createSuperAdmin(
                    new UserCreateDto(
                            superAdminUsername,
                            superAdminEmail,
                            superAdminPassword
                    )
            );

            log.info("Initializing super admin: {}", superAdmin);
        } else {
            superAdmin = superAdminInitService.updateSuperAdmin(
                    superAdmin,
                    superAdminUsername,
                    superAdminEmail,
                    superAdminPassword
            );

            log.info("Updating super admin: {}", superAdmin);
        }

        log.info("Super admin password: {}", superAdminPassword);
    }
}
