package org.y9nba.app.dao.entity.embedded;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.*;
import org.y9nba.app.constant.ProviderType;

import java.io.Serializable;

@Embeddable
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode
@Getter
@Setter
public class ProviderPK implements Serializable {
    @Column(name = "user_id")
    private Long userId;

    @Column(name = "provider_type", nullable = false, length = 10)
    @Enumerated(EnumType.STRING)
    private ProviderType providerType;
}
