package org.y9nba.app.initializer;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import org.y9nba.app.dto.user.UserCreateDto;
import org.y9nba.app.dao.entity.User;
import org.y9nba.app.service.impl.user.UserServiceImpl;
import org.y9nba.app.util.PasswordUtil;

@Component
@Slf4j
public class SuperAdminInitializer {

    private final UserServiceImpl userService;
    private final PasswordUtil passwordUtil;

    @Value("${initializer.super_admin.username}")
    private String superAdminUsername;
    @Value("${initializer.super_admin.password}")
    private String superAdminPassword;
    @Value("${initializer.super_admin.email}")
    private String superAdminEmail;

    public SuperAdminInitializer(UserServiceImpl userService, PasswordUtil passwordUtil) {
        this.userService = userService;
        this.passwordUtil = passwordUtil;
    }

    @EventListener(ApplicationReadyEvent.class)
    public void initializeSuperAdmin() {
        User superAdmin = userService.getSuperAdmin();

        if (superAdmin == null) {
            superAdmin = userService.createSuperAdmin(
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

            superAdmin = userService.updateSuperAdmin(superAdmin);

            log.info("Updating super admin: {}", superAdmin);
        }

        log.info("Super admin password: {}", superAdminPassword);
    }
}
