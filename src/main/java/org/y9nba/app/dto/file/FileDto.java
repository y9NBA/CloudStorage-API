package org.y9nba.app.dto.file;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.y9nba.app.dto.auditlog.AuditLogDto;
import org.y9nba.app.dto.fileaccess.FileAccessDto;
import org.y9nba.app.mapper.GeneralMapper;
import org.y9nba.app.model.FileModel;

import java.time.LocalDateTime;
import java.util.Set;

@Data
@NoArgsConstructor
public class FileDto {
    private Long id;
    private Long userId;
    private String fileName;
    private Long fileSize;
    private String mimeType;
    private String url;
    private LocalDateTime createdAt;
    private Set<FileAccessDto> fileAccesses;
    private Set<AuditLogDto> auditLogs;

    public FileDto(FileModel model) {
        this.id = model.getId();
        this.userId = model.getUser().getId();
        this.fileName = model.getFileName();
        this.fileSize = model.getFileSize();
        this.mimeType = model.getMimeType();
        this.url = model.getUrl();
        this.createdAt = model.getCreatedAt();
        this.fileAccesses = GeneralMapper.toFileAccessDto(model.getFileAccesses());
        this.auditLogs = GeneralMapper.toAuditLogDto(model.getAuditLogs());
    }
}
