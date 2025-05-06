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
    private String bucketName;
    private String folderURL;
    private boolean isPublic;
    private LocalDateTime createdAt;
    private Set<FileAccessDto> fileAccesses;
    private Set<AuditLogDto> auditLogs;

    public FileDto(FileModel model) {
        this.id = model.getId();
        this.userId = model.getUser().getId();
        this.fileName = model.getFileName();
        this.fileSize = model.getFileSize();
        this.mimeType = model.getMimeType();
        this.isPublic = model.getIsPublic();
        this.createdAt = model.getCreatedAt();
        this.fileAccesses = GeneralMapper.toFileAccessDto(model.getFileAccesses());
        this.auditLogs = GeneralMapper.toAuditLogDto(model.getAuditLogs());
        setFolderURLAndBucket(model.getUrl());
    }

    public String generateFileURL() {
        return bucketName + "/" + folderURL + "/" + fileName;
    }

    public void setFolderURLAndBucket(String absFileUrl) {
        this.bucketName = absFileUrl.substring(0, absFileUrl.indexOf("/"));

        if (absFileUrl.lastIndexOf("/") == absFileUrl.indexOf("/")) {
            this.folderURL = "";
        } else {
            this.folderURL = absFileUrl.replace(this.bucketName + "/", "").replace("/" + fileName, "");
        }
    }
}
