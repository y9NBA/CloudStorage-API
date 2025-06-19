package org.y9nba.app.dto.auth;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class OAuth2ResponseDTO {
    private final String oauthUrl;
    private final String successUrl;
}
