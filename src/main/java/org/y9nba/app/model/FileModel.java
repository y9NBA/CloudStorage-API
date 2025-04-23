package org.y9nba.app.model;

import jakarta.persistence.*;
import lombok.*;
import org.y9nba.app.dto.file.FileCreateDto;
import org.y9nba.app.dto.file.FileUpdateDto;

import java.time.LocalDateTime;
import java.util.Set;

@Entity
@Table(name = "file")
@Getter
@Setter
@NoArgsConstructor
public class FileModel {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private UserModel user;

    @Column(name = "file_name", nullable = false)
    private String fileName;

    @Column(name = "file_size", nullable = false)
    private Long fileSize;

    @Column(name = "mime_type", nullable = false)
    private String mimeType;

    @Column(nullable = false, unique = true)
    private String url;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    @OneToMany(mappedBy = "file", fetch = FetchType.EAGER)
    private Set<FileAccessModel> fileAccesses;

    @OneToMany(mappedBy = "file", fetch = FetchType.EAGER)
    private Set<AuditLogModel> auditLogs;

    public FileModel(FileCreateDto dto) {
        this.fileName = dto.getFileName();
        this.fileSize = dto.getFileSize();
        this.mimeType = dto.getMimeType();
        this.url = dto.getUrl();
        this.user = dto.getUser();
    }

    public FileModel(FileUpdateDto dto) {
        this.id = dto.getId();
        this.fileName = dto.getFileName();
        this.fileSize = dto.getFileSize();
        this.mimeType = dto.getMimeType();
        this.url = dto.getUrl();
        this.user = dto.getUser();
    }
}

