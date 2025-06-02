package org.y9nba.app.dto.fileaccess;

import lombok.Getter;
import lombok.Setter;
import org.y9nba.app.constant.Access;
import org.y9nba.app.dao.entity.FileAccess;
import org.y9nba.app.dao.entity.File;
import org.y9nba.app.dao.entity.User;

@Getter
@Setter
public class FileAccessUpdateDto {
    private Long id;
    private File file;
    private User user;
    private Access accessLevel;

    public FileAccessUpdateDto(FileAccess model) {
        this.id = model.getId();
        this.file = model.getFile();
        this.user = model.getUser();
        this.accessLevel = model.getAccessLevel();
    }
}
