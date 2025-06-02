package org.y9nba.app.dto.session;

import lombok.Getter;
import lombok.Setter;
import org.y9nba.app.dao.entity.Session;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
public class SessionDto {
    private final UUID id;
    private final String deviceType;
    private final String operatingSystem;
    private final String browser;
    private final String ipAddress;
    private final LocalDateTime loginTime;
    private final LocalDateTime lastActive;

    public SessionDto(Session session) {
        this.id = session.getId();
        this.deviceType = session.getDeviceType();
        this.operatingSystem = session.getOperatingSystem();
        this.browser = session.getBrowser();
        this.ipAddress = session.getIpAddress();
        this.loginTime = session.getLoginTime();
        this.lastActive = session.getLastActive();
    }
}
