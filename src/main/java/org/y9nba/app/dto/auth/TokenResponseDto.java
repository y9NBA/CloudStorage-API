package org.y9nba.app.dto.auth;

import lombok.Getter;

@Getter
public class TokenResponseDto {

    private final String accessToken;

    private final String refreshToken;

    public TokenResponseDto(String token, String refreshToken) {
        this.accessToken = token;
        this.refreshToken = refreshToken;
    }
}
