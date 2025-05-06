package org.y9nba.app.dto.file;

import lombok.Getter;
import lombok.Setter;
import org.y9nba.app.dto.auditlog.AuditLogDto;
import org.y9nba.app.dto.fileaccess.FileAccessDto;
import org.y9nba.app.mapper.GeneralMapper;
import org.y9nba.app.model.FileModel;
import org.y9nba.app.model.UserModel;

import java.time.LocalDateTime;
import java.util.Set;

@Setter
@Getter
public class FileUpdateDto {
    private Long id;
    private String fileName;
    private Long fileSize;
    private String mimeType;
    private String url;
    private UserModel user;
    private Boolean isPublic;
    private LocalDateTime createdAt;
    private Set<FileAccessDto> fileAccesses;
    private Set<AuditLogDto> auditLogs;

    public FileUpdateDto(FileModel model) {
        this.id = model.getId();
        this.fileName = model.getFileName();
        this.fileSize = model.getFileSize();
        this.mimeType = model.getMimeType();
        this.url = model.getUrl();
        this.user = model.getUser();
        this.isPublic = model.isPublic();
        this.createdAt = model.getCreatedAt();
        this.fileAccesses = GeneralMapper.toFileAccessDto(model.getFileAccesses());
        this.auditLogs = GeneralMapper.toAuditLogDto(model.getAuditLogs());
    }

    public void makePublic() {
        this.isPublic = true;
    }

    public void makePrivate() {
        this.isPublic = false;
    }
}
