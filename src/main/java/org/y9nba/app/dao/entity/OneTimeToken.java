package org.y9nba.app.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.y9nba.app.constant.OneTimeTokenType;
import org.y9nba.app.dto.onetimetoken.OneTimeTokenCreateDto;

import java.util.UUID;

@Entity
@Table(name = "one_time_token")
@Getter
@Setter
@NoArgsConstructor
public class OneTimeTokenModel {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "token", length = 320)
    private String token;

    @Column(name = "type", length = 20)
    @Enumerated(EnumType.STRING)
    private OneTimeTokenType type;

    @Column(name = "is_used")
    private boolean isUsed;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private UserModel user;

    public OneTimeTokenModel(OneTimeTokenCreateDto dto) {
        this.token = dto.getToken();
        this.type = dto.getType();
        this.isUsed = false;
        this.user = dto.getUserModel();
    }
}
