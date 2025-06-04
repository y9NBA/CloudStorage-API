package org.y9nba.app.dto.search;

import lombok.Getter;
import org.y9nba.app.dao.entity.User;

import java.time.LocalDateTime;

@Getter
public class AdminInfoDto {
    private final Long id;
    private final String username;
    private final String email;
    private final LocalDateTime createdAt;

    public AdminInfoDto(User model) {
        this.id = model.getId();
        this.username = model.getUsername();
        this.email = model.getEmail();
        this.createdAt = model.getCreatedAt();
    }
}
