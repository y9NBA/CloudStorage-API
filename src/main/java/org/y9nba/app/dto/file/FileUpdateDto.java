package org.y9nba.app.dto.file;

import lombok.Getter;
import lombok.Setter;
import org.y9nba.app.model.FileModel;
import org.y9nba.app.model.UserModel;

@Setter
@Getter
public class FileUpdateDto {
    private Long id;
    private String fileName;
    private Long fileSize;
    private String mimeType;
    private String url;
    private UserModel user;

    public FileUpdateDto(FileModel model) {
        this.id = model.getId();
        this.fileName = model.getFileName();
        this.fileSize = model.getFileSize();
        this.mimeType = model.getMimeType();
        this.url = model.getUrl();
        this.user = model.getUser();
    }
}
