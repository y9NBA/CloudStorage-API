package org.y9nba.app.dto.file;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.y9nba.app.model.UserModel;

@AllArgsConstructor
@Getter
public class FileCreateDto {
    private String fileName;
    private Long fileSize;
    private String mimeType;
    private String url;
    private UserModel user;
}