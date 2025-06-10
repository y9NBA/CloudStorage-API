package org.y9nba.app.dao.entity;

import jakarta.persistence.*;
import lombok.*;
import org.y9nba.app.dto.file.FileCreateDto;
import org.y9nba.app.dto.file.FileUpdateDto;

import java.time.LocalDateTime;
import java.util.Set;

@Entity
@Table(name = "files")
@Getter
@Setter
@NoArgsConstructor
public class File {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @Column(name = "file_name", nullable = false)
    private String fileName;

    @Column(name = "file_size", nullable = false)
    private Long fileSize;

    @Column(name = "mime_type", nullable = false)
    private String mimeType;

    @Column(nullable = false, unique = true, length = 1000)
    private String url;

    @Column(nullable = false)
    private Boolean isPublic = false;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt = LocalDateTime.now();

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    @OneToMany(mappedBy = "file", fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    private Set<FileAccess> fileAccesses;

    @OneToMany(mappedBy = "file", fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    private Set<AuditLog> auditLogs;

    public File(FileCreateDto dto) {
        this.fileName = dto.getFileName();
        this.fileSize = dto.getFileSize();
        this.mimeType = dto.getMimeType();
        this.url = dto.getUrl();
        this.user = dto.getUser();
    }

    public File(FileUpdateDto dto) {
        this.id = dto.getId();
        this.fileName = dto.getFileName();
        this.fileSize = dto.getFileSize();
        this.mimeType = dto.getMimeType();
        this.url = dto.getUrl();
        this.user = dto.getUser();
        this.isPublic = dto.getIsPublic();
        this.createdAt = dto.getCreatedAt();
        this.updatedAt = LocalDateTime.now();
    }

    public Boolean isPublic() {
        return this.isPublic;
    }
}

