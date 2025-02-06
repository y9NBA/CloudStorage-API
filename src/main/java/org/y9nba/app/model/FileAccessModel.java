package org.y9nba.app.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import org.y9nba.app.constant.Access;

@Entity
@Table(name = "file_access")
@Data
@NoArgsConstructor
public class FileAccessModel {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "file_id")
    private FileModel file;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private UserModel user;

    @Column(name = "access_level", nullable = false)
    private Access accessLevel;
}
