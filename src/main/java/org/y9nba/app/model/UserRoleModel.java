package org.y9nba.app.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

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

    @ManyToOne
    @MapsId("roleId")
    @JoinColumn(name = "role_id")
    private RoleModel role;

    @Embeddable
    @Data
    public static class UserRoleId implements Serializable {
        private Long userId;
        private Long roleId;
    }
}

