package org.y9nba.app.constant;

import lombok.Getter;

import java.util.Set;

@Getter
public enum Role {
    ROLE_USER(Set.of(
            "MANIPULATE_STORAGE",
            "UPDATE_USERNAME",
            "UPDATE_PASSWORD",
            "UPDATE_EMAIL",
            "UPDATE_PROFILE",
            "DELETE_PROFILE"
    )),
    ROLE_ADMIN(Set.of(
            "VIEW_PUBLIC_FILES",
            "REVOKE_PUBLIC_FILES",
            "BAN_HAMMER",
            "INFO_USERS",
            "UPDATE_USERNAME",
            "UPDATE_PASSWORD",
            "DELETE_PROFILE"
    )),
    ROLE_SUPER_ADMIN(Set.of(
            "VIEW_PUBLIC_FILES",
            "REVOKE_PUBLIC_FILES",
            "BAN_HAMMER",
            "INFO_USERS",
            "INFO_ADMINS",
            "CREATE_ADMIN",
            "DELETE_ADMIN"
    ));

    private final Set<String> authorities;

    Role(Set<String> authorities) {
        this.authorities = authorities;
    }
}

