package org.y9nba.app.model;

import jakarta.persistence.*;
import lombok.*;
import org.y9nba.app.constant.Role;

import java.io.Serializable;

@Entity
@Table(name = "user_role")
@Getter
@Setter
@NoArgsConstructor
public class UserRoleModel {
    @EmbeddedId
    private UserRoleId id;

    @ManyToOne
    @MapsId("userId")
    @JoinColumn(name = "user_id", nullable = false)
    private UserModel user;

    @Embeddable
    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class UserRoleId implements Serializable {

        private Long userId;

        @Column(name = "role")
        @Enumerated(EnumType.STRING)
        private Role role;
    }

    public UserRoleModel(UserModel user, Role role) {
        this.id = new UserRoleId(user.getId(), role);
        this.user = user;
    }
}

