package org.y9nba.app.dto.file;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.y9nba.app.dao.entity.File;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
public class FilePresentDto {
    private Long id;
    private Long userId;
    private String fileName;
    private Long fileSize;
    private String mimeType;
    private String bucketName;
    private String folderURL;
    private boolean isPublic;
    private boolean isShared;
    private LocalDateTime createdAt;

    public FilePresentDto(File model) {
        this.id = model.getId();
        this.userId = model.getUser().getId();
        this.fileName = model.getFileName();
        this.fileSize = model.getFileSize();
        this.mimeType = model.getMimeType();
        this.isPublic = model.getIsPublic();
        this.createdAt = model.getCreatedAt();
        this.isShared = model.getFileAccesses() != null && model.getFileAccesses().size() > 1;
        setFolderURLAndBucket(model.getUrl());
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
