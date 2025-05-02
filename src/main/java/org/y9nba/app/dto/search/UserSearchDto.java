package org.y9nba.app.dto.search;

import lombok.Getter;
import org.y9nba.app.model.UserModel;

@Getter
public class UserSearchDto {
    private final Long id;
    private final String username;
    private final String email;

    public UserSearchDto(UserModel model) {
        this.id = model.getId();
        this.username = model.getUsername();
        this.email = model.getEmail();
    }
}
