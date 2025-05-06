package org.y9nba.app.dto.fileaccess;

import lombok.Getter;
import lombok.Setter;
import org.y9nba.app.constant.Access;
import org.y9nba.app.model.FileAccessModel;
import org.y9nba.app.model.FileModel;
import org.y9nba.app.model.UserModel;

@Getter
@Setter
public class FileAccessUpdateDto {
    private Long id;
    private FileModel file;
    private UserModel user;
    private Access accessLevel;

    public FileAccessUpdateDto(FileAccessModel model) {
        this.id = model.getId();
        this.file = model.getFile();
        this.user = model.getUser();
        this.accessLevel = model.getAccessLevel();
    }
}
