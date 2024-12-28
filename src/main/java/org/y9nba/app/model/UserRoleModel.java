package org.y9nba.app.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.y9nba.app.model.user.Role;

import java.io.Serializable;

@Entity
@Table(name = "user_role")
@Data
@NoArgsConstructor
public class UserRoleModel {
    @EmbeddedId
    private UserRoleId id;

    @ManyToOne
    @MapsId("userId")
    @JoinColumn(name = "user_id")
    private UserModel user;

    @Column(name = "role", nullable = false)
    private Role role;

    @Embeddable
    @Data
    public static class UserRoleId implements Serializable {
        private Long userId;
        private Long roleId;
    }
}

