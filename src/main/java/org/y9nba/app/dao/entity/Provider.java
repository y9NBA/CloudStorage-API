package org.y9nba.app.dao.entity;

import jakarta.persistence.*;
import lombok.*;
import org.y9nba.app.constant.ProviderType;
import org.y9nba.app.dao.entity.embedded.ProviderPK;

import java.time.LocalDateTime;

@Entity
@Table(name = "providers")
@Getter
@Setter
@NoArgsConstructor
public class Provider {
    @EmbeddedId
    private ProviderPK id;

    @MapsId("userId")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @Column(name = "provider_id", nullable = false)
    private Long providerId;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    public ProviderType getProviderType() {
        return id.getProviderType();
    }
}
