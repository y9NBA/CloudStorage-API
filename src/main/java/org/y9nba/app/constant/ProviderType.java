package org.y9nba.app.constant;

public enum ProviderType {
    GITHUB,
    GOOGLE;

    public String getProviderName() {
        return this.name().toLowerCase();
    }
}
