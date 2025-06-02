package org.y9nba.app.dao.entity;

import jakarta.persistence.*;
import lombok.*;

import org.y9nba.app.constant.Access;
import org.y9nba.app.dto.fileaccess.FileAccessCreateDto;
import org.y9nba.app.dto.fileaccess.FileAccessUpdateDto;

@Entity
@Table(name = "file_accesses")
@Getter
@Setter
@NoArgsConstructor
public class FileAccess {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "file_id")
    private File file;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @Column(name = "access_level", nullable = false, length = 50)
    @Enumerated(EnumType.STRING)
    private Access accessLevel;

    public FileAccess(FileAccessCreateDto dto) {
        this.user = dto.getUser();
        this.file = dto.getFile();
        this.accessLevel = dto.getAccessLevel();
    }

    public FileAccess(FileAccessUpdateDto dto) {
        this.id = dto.getId();
        this.user = dto.getUser();
        this.file = dto.getFile();
        this.accessLevel = dto.getAccessLevel();
    }
}
