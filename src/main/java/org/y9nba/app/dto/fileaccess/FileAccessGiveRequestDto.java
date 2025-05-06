package org.y9nba.app.dto.fileaccess;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.y9nba.app.constant.Access;

@AllArgsConstructor
@Getter
public class FileAccessGiveRequestDto {
    private Long collaboratorId;
    private int accessLevelCode;

    public Access extractAccessLevel() {
        if (accessLevelCode == 0) {
            return Access.ACCESS_READER;
        } else if (accessLevelCode == 1) {
            return Access.ACCESS_EDITOR;
        } else {
            return null;
        }
    }
}
