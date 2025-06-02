package org.y9nba.app.dto.fileaccess;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.y9nba.app.constant.Access;
import org.y9nba.app.dao.entity.File;
import org.y9nba.app.dao.entity.User;

@AllArgsConstructor
@Getter
public class FileAccessCreateDto {
    private File file;
    private User user;
    private Access accessLevel;
}
