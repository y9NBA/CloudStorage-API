package org.y9nba.app.dto.onetimetoken;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.y9nba.app.constant.OneTimeTokenType;
import org.y9nba.app.model.UserModel;

@AllArgsConstructor
@Getter
public class OneTimeTokenCreateDto {
    private UserModel userModel;
    private String token;
    private OneTimeTokenType type;
}
