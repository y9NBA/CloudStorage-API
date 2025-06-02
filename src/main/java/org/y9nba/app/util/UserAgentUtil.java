package org.y9nba.app.util;

import org.springframework.stereotype.Component;
import org.y9nba.app.dto.useragent.DeviceInfoDto;
import ua_parser.Client;
import ua_parser.Parser;

@Component
public class UserAgentUtil {
    private final Parser uaParser = new Parser();

    public DeviceInfoDto parseUserAgent(String userAgentString) {
        Client client = uaParser.parse(userAgentString);

        String deviceType = client.device.family;
        String os = client.os.family + " " + client.os.major;
        String browser = client.userAgent.family + " " + client.userAgent.major;

        return new DeviceInfoDto(deviceType, os, browser);
    }
}
