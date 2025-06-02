package org.y9nba.app.dao.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.y9nba.app.constant.OneTimeTokenType;
import org.y9nba.app.dto.onetimetoken.OneTimeTokenCreateDto;

import java.util.UUID;

@Entity
@Table(name = "one_time_tokens")
@Getter
@Setter
@NoArgsConstructor
public class OneTimeToken {     // TODO: перестать хранить токен в бд, билдить в токен UUID one time и вновь передавать токен в ссылке
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "type", length = 20)
    @Enumerated(EnumType.STRING)
    private OneTimeTokenType type;

    @Column(name = "is_used")
    private boolean isUsed = false;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    public OneTimeToken(OneTimeTokenCreateDto dto) {
        this.type = dto.getType();
        this.user = dto.getUser();
    }
}
