package org.y9nba.app.dto.onetimetoken;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.y9nba.app.constant.OneTimeTokenType;
import org.y9nba.app.dao.entity.User;

@AllArgsConstructor
@Getter
public class OneTimeTokenCreateDto {
    private User user;
    private OneTimeTokenType type;
}
