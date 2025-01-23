package org.y9nba.app.model.user;

public enum Role {

    ROLE_USER("ROLE_USER"),
    ROLE_ADMIN("ROLE_ADMIN");

    public final String roleName;

    Role(String roleName) {
        this.roleName = roleName;
    }

    public final String getRoleName() {
        return this.roleName;
    }
}

