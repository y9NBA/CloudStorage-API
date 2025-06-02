package org.y9nba.app.dto.useragent;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class DeviceInfoDto {
    private String deviceType;
    private String operatingSystem;
    private String browser;

    public DeviceInfoDto(String deviceType, String operatingSystem, String browser) {
        this.deviceType = deviceType;
        this.operatingSystem = operatingSystem;
        this.browser = browser;
    }
}
