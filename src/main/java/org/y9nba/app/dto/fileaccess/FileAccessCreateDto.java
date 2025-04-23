package org.y9nba.app.dto.fileaccess;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.y9nba.app.constant.Access;
import org.y9nba.app.model.FileModel;
import org.y9nba.app.model.UserModel;

@AllArgsConstructor
@Getter
public class FileAccessCreateDto {
    private FileModel file;
    private UserModel user;
    private Access accessLevel;
}
