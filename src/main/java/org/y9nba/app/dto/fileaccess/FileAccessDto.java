package org.y9nba.app.dto.fileaccess;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.y9nba.app.constant.Access;
import org.y9nba.app.model.FileAccessModel;

@Data
@NoArgsConstructor
public class FileAccessDto {
    private Long id;
    private Long fileId;
    private Long userId;
    private Access accessLevel;

    public FileAccessDto(FileAccessModel model) {
        this.id = model.getId();
        this.fileId = model.getFile().getId();
        this.userId = model.getUser().getId();
        this.accessLevel = model.getAccessLevel();
    }
}
